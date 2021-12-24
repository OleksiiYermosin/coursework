package ua.training.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.project.dto.SearchDTO;
import ua.training.project.dto.StudentRatingDTO;
import ua.training.project.dto.UserCourseDTO;
import ua.training.project.dto.UserEvaluationDTO;
import ua.training.project.dto.input.StudentCourseInfoSearchDTO;
import ua.training.project.entities.*;
import ua.training.project.exceptions.IncorrectUserException;
import ua.training.project.repositories.*;
import ua.training.project.utils.ConstantHolder;

import static ua.training.project.utils.ConstantHolder.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class InstructorService {

    private final UserRepository userRepository;

    private final MistakeRepository mistakeRepository;

    private final EventRepository eventRepository;

    private final TrainingRepository trainingRepository;
    private final CourseRepository courseRepository;
    private AttendanceRepository attendanceRepository;
    private MistakeService mistakeService;

    @Autowired
    public InstructorService(UserRepository userRepository, MistakeRepository mistakeRepository,
                             EventRepository eventRepository, TrainingRepository trainingRepository,
                             CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.mistakeRepository = mistakeRepository;
        this.eventRepository = eventRepository;
        this.trainingRepository = trainingRepository;
        this.courseRepository = courseRepository;
    }

    @Autowired
    public void setMistakeService(MistakeService mistakeService) {
        this.mistakeService = mistakeService;
    }

    @Autowired
    public void setAttendanceRepository(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public UserCourseDTO getUserEvaluationInfo(Long userId) {
        return UserCourseDTO.builder().user(userRepository.findById(userId).orElseThrow(IncorrectUserException::new)).
                mistakes(mistakeRepository.findAll()).events(eventRepository.findAll()).build();
    }

    public Page<Training> getUserCourses(StudentCourseInfoSearchDTO searchDTO, Long userId){
        return trainingRepository
                .findByCourseNameContainingAndEventNameContainingAndCourseCategoryNameContainingAndUserIdEquals(
                        searchDTO.getCourseName(), searchDTO.getEventName(), searchDTO.getCategoryName(),
                        userId, PageRequest.of(searchDTO.getPageNumber(), MAX_RECORDS_PER_PAGE,
                                Sort.by(Sort.Direction.valueOf(searchDTO.getSortDirection()), searchDTO.getSort()))
                );
    }

    @Transactional
    public void evaluateUser(Long studentId, UserEvaluationDTO userEvaluationDTO) {
        User user = userRepository.findById(studentId).orElseThrow(IncorrectUserException::new);
        Course course = courseRepository.getById(userEvaluationDTO.getCourseId());
        List<Mistake> mistakes = mistakeService.getMistakesById(userEvaluationDTO.getMistakesList());
        if (!isAvailableLessonsExist(user, course)) {
            throw new RuntimeException();
        }
        if (userEvaluationDTO.getEventId() != -1) {
            Training userTraining = Training.builder().user(user).event(eventRepository.getById(userEvaluationDTO.getEventId()))
                    .course(course).mistakes(mistakes).date(LocalDate.now()).mark(calculateMark(mistakes)).build();
            trainingRepository.save(userTraining);
        } else {
            addMissedLesson(user, course);
        }
    }

    @Transactional
    public Page<StudentRatingDTO> prepareStudentRatingDTO(SearchDTO searchDTO) {
        Page<StudentRatingDTO> studentRatingDTO = attendanceRepository.findAllByPredicate(
                searchDTO.getName(), searchDTO.getSurname(), searchDTO.getCourseName(), searchDTO.getTotalLessons(),
                searchDTO.getAttendedLessons(), searchDTO.getMissedLessons(), PageRequest.of(searchDTO.getPageNumber(),
                        MAX_RECORDS_PER_PAGE, Sort.by(Sort.Direction.valueOf(searchDTO.getSort().split(":")[1]),
                                searchDTO.getSort().split(":")[0])));
        studentRatingDTO.forEach(d -> d.setAverageMark(calculateAverageMark(
                trainingRepository.findByCourseIdAndUserId(d.getCourse().getId(), d.getUser().getId()))));
        return studentRatingDTO;
    }

    private void addMissedLesson(User user, Course course) {
        Attendance attendance = attendanceRepository.findByUserAndCourse(user, course);
        attendance.setMissedLessonsAmount(attendance.getMissedLessonsAmount() + 1);
        attendanceRepository.save(attendance);
    }

    private boolean isAvailableLessonsExist(User user, Course course) {
        Attendance attendance = attendanceRepository.findByUserAndCourse(user, course);
        return attendance.getAttendedLessonsAmount() + attendance.getMissedLessonsAmount() < course.getLessonsAmount();
    }

    private int calculateMark(List<Mistake> mistakes) {
        return Math.max(MAX_MARK - mistakes.stream().mapToInt(Mistake::getPenaltyPoint).sum(), 0);
    }

    private BigDecimal calculateAverageMark(List<Training> trainingsList) {
        BigDecimal result = trainingsList.size() == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(trainingsList.stream().mapToInt(Training::getMark).sum() / trainingsList.size());
        return result.setScale(2, RoundingMode.CEILING);

    }

}
