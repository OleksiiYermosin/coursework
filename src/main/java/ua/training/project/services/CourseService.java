package ua.training.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import ua.training.project.dto.CourseDTO;
import ua.training.project.dto.CourseInfoDTO;
import ua.training.project.dto.CourseSearchDTO;
import ua.training.project.dto.UserCoursesDTO;
import ua.training.project.dto.input.StudentCoursesDTO;
import ua.training.project.entities.*;
import ua.training.project.exceptions.IncorrectCourseException;
import ua.training.project.exceptions.NotEnoughMoneyException;
import ua.training.project.repositories.*;
import ua.training.project.utils.AttendanceKey;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.*;

import static ua.training.project.utils.ConstantHolder.*;

@Service
public class CourseService {

    private final TrainingRepository trainingRepository;

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private final AttendanceRepository attendanceRepository;

    private final CategoryRepository categoryRepository;

    private UserService userService;

    @Autowired
    public CourseService(TrainingRepository trainingRepository, UserRepository userRepository,
                         CourseRepository courseRepository, AttendanceRepository attendanceRepository,
                         CategoryRepository categoryRepository) {
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.attendanceRepository = attendanceRepository;
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    public CourseDTO findCoursesByName(CourseSearchDTO searchCourseDTO, Long userId){
        Page<Course> courses = courseRepository.findByNameContaining(searchCourseDTO.getCourseName(), PageRequest.of(
                searchCourseDTO.getPageNumber(),MAX_RECORDS_PER_PAGE, Sort.by(Sort.Direction.valueOf(
                        searchCourseDTO.getSortDirection()), searchCourseDTO.getSort())));
        Map<Long, Boolean> userCourseMap = new HashMap<>();
        System.out.println(courses);
        for (Course course : courses) {
            boolean isCurrentUserEnrolled = userRepository.getById(userId).getAttendances().stream().
                    anyMatch(a -> a.getCourse().getId().equals(course.getId()));
            userCourseMap.put(course.getId(), isCurrentUserEnrolled);
        }
        return new CourseDTO(courses, userCourseMap);
    }

    @Transactional
    public UserCoursesDTO getCoursesAndTrainingsOfUser(Long userId, StudentCoursesDTO studentCoursesDTO){
        Page<Course> courses = attendanceRepository.findCoursesByUserIdAndCourseName(userId, studentCoursesDTO.getCourseName(),
                PageRequest.of(studentCoursesDTO.getPageNumber(), MAX_RECORDS_PER_PAGE));
        Map<Long, Long> passedEventsMap = new HashMap<>();
        Map<Long, Long> failedEventsMap = new HashMap<>();
        for (Course course : courses){
            List<Training> trainingList = trainingRepository.findByCourseIdAndUserId(course.getId(), userId);
            passedEventsMap.put(course.getId(), trainingList.stream().filter(t -> t.getMark() >= ACCEPTANCE_MARK).count());
            failedEventsMap.put(course.getId(), trainingList.stream().filter(t -> t.getMark() < ACCEPTANCE_MARK).count());
        }
        return new UserCoursesDTO(courses, passedEventsMap, failedEventsMap);
    }

    @Transactional
    public CourseInfoDTO getCourseDescription(Long userId, Long courseId) {
        Set<Attendance> attendances = userRepository.getById(userId).getAttendances();
        Attendance courseAttendance = attendances.stream().
                filter(a -> a.getCourse().getId().equals(courseId)).findFirst().orElseThrow(IncorrectCourseException::new);
        List<Training> trainings = trainingRepository.findByCourseIdAndUserId(courseId, userId);
        return new CourseInfoDTO(courseAttendance.getCourse().getLessonsAmount(), courseAttendance.getMissedLessonsAmount(),
                courseAttendance.getAttendedLessonsAmount(), courseAttendance.getRate(), trainings,
                courseAttendance.getCourse().getName(), courseId);
    }

    public void enrollUserInNewCourse(Long userId, Long courseId){
        Course course = courseRepository.getById(courseId);
        User user;
        try{
            user = userService.updateUserBalance(userId, course.getPrice().negate());
        }catch (RuntimeException exception){
            throw new NotEnoughMoneyException();
        }
        AttendanceKey key = new AttendanceKey(userId, courseId);
        Attendance attendance = new Attendance(key, user, course, 0, 0, false, null);
        attendanceRepository.save(attendance);
    }

    @Transactional
    public void addNewCourse(Course course, Long categoryId){
        course.setRating(BigDecimal.valueOf(0.00));
        course.setCategory(categoryRepository.getById(categoryId));
        courseRepository.save(course);
    }

    public List<Category> getCategories(){
        return categoryRepository.findAll();
    }

    public void rateCourse(Long userId, Long courseId, Integer rate){
        Attendance attendance = userRepository.getById(userId).getAttendances().stream().
                filter(a -> a.getCourse().getId().equals(courseId)).findFirst().orElseThrow(IncorrectCourseException::new);
        attendance.setRate(rate);
        attendanceRepository.save(attendance);
        updateCourseRating(courseId);
    }

    private void updateCourseRating(Long courseId){
        Course course = courseRepository.getById(courseId);
        long count = course.getAttendances().stream().filter(a -> a.getRate() != null).count();
        int sum = course.getAttendances().stream().filter(a -> a.getRate() != null).mapToInt(Attendance::getRate).sum();
        if (count != 0){
            course.setRating(BigDecimal.valueOf(sum/count));
            courseRepository.save(course);
        }
    }

}
