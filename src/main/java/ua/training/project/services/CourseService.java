package ua.training.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.project.dto.CourseDTO;
import ua.training.project.dto.CourseInfoDTO;
import ua.training.project.dto.UserCoursesDTO;
import ua.training.project.entities.Attendance;
import ua.training.project.entities.Course;
import ua.training.project.entities.Training;
import ua.training.project.exceptions.IncorrectCourseException;
import ua.training.project.repositories.AttendanceRepository;
import ua.training.project.repositories.CourseRepository;
import ua.training.project.repositories.TrainingRepository;
import ua.training.project.repositories.UserRepository;
import ua.training.project.utils.AttendanceKey;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ua.training.project.utils.ConstantHolder.*;

@Service
public class CourseService {

    private final TrainingRepository trainingRepository;

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private final AttendanceRepository attendanceRepository;

    @Autowired
    public CourseService(TrainingRepository trainingRepository, UserRepository userRepository,
                         CourseRepository courseRepository, AttendanceRepository attendanceRepository) {
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional
    public List<CourseDTO> findCoursesByName(String name, Long userId){
        List<Course> courses = courseRepository.findByNameContaining(name);
        List<CourseDTO> resultList = new LinkedList<>();
        for (Course course : courses) {
            boolean isCurrentUserEnrolled = userRepository.getById(userId).getAttendances().stream().
                    anyMatch(c -> c.getCourse().getId().equals(course.getId()));
            resultList.add(new CourseDTO(course, isCurrentUserEnrolled));
        }
        return resultList;
    }

    @Transactional
    public List<UserCoursesDTO> getCoursesAndTrainingsOfUser(Long userId){
        List<Course> courses = userRepository.getById(userId).getAttendances().
                stream().map(Attendance::getCourse).collect(Collectors.toList());
        List<UserCoursesDTO> resultList = new LinkedList<>();
        for (Course course : courses){
            List<Training> trainingList = trainingRepository.findByCourseIdAndUserId(course.getId(), userId);
            resultList.add(new UserCoursesDTO(course, trainingList.stream().filter(t -> t.getMark() >= ACCEPTANCE_MARK).count(),
                    trainingList.stream().filter(t -> t.getMark() < ACCEPTANCE_MARK).count()));
        }
        return resultList;
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

    @Transactional
    public void enrollUserInNewCourse(Long userId, Long courseId){
        AttendanceKey key = new AttendanceKey(userId, courseId);
        Attendance attendance = new Attendance(key, userRepository.getById(userId), courseRepository.getById(courseId),
                0, 0, false, null);
        attendanceRepository.save(attendance);
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
