package ua.training.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.project.dto.UserCourseDTO;
import ua.training.project.dto.UserEvaluationDTO;
import ua.training.project.entities.*;
import ua.training.project.repositories.*;
import static ua.training.project.utils.ConstantHolder.*;

import java.time.LocalDate;
import java.util.List;

@Service
public class InstructorService {

    private final UserRepository userRepository;

    private final MistakeRepository mistakeRepository;

    private final EventRepository eventRepository;

    private final TrainingRepository trainingRepository;

    private AttendanceRepository attendanceRepository;

    private final CourseRepository courseRepository;

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
        return UserCourseDTO.builder().
                user(userRepository.findById(userId).orElseThrow(RuntimeException::new)).
                mistakes(mistakeRepository.findAll()).
                events(eventRepository.findAll()).build();
    }

    @Transactional
    public void evaluateUser(Long studentId, UserEvaluationDTO userEvaluationDTO) {
        User user = userRepository.findById(studentId).orElseThrow(RuntimeException::new);
        Course course = courseRepository.findById(userEvaluationDTO.getCourseId()).orElseThrow(RuntimeException::new);
        List<Mistake> mistakes = mistakeService.getMistakesById(userEvaluationDTO.getMistakesList());
        if(!isAvailableLessonsExist(user, course)){
            return;
        }
        if(userEvaluationDTO.getEventId() != -1) {
            Training userTraining = Training.builder().user(user)
                    .event(eventRepository.findById(userEvaluationDTO.getEventId()).orElseThrow(RuntimeException::new))
                    .course(course).mistakes(mistakes).date(LocalDate.now()).mark(calculateMark(mistakes)).build();
            trainingRepository.save(userTraining);
        }else {
            addMissedLesson(user, course);
        }
    }

    private void addMissedLesson(User user, Course course){
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

}
