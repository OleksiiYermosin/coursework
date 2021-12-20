package ua.training.project.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.training.project.dto.StudentRatingDTO;
import ua.training.project.entities.Attendance;
import ua.training.project.entities.Course;
import ua.training.project.entities.User;
import ua.training.project.utils.AttendanceKey;

public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceKey> {

    Attendance findByUserAndCourse(User user, Course course);

    @Query("SELECT new ua.training.project.dto.StudentRatingDTO(u, a, c) FROM Attendance a " +
            "INNER JOIN a.course c ON a.course.id = c.id INNER JOIN a.user u ON a.user.id = u.id " +
            "WHERE c.id = a.course.id AND u.id = a.user.id AND " +
            "u.name LIKE %?1% AND u.surname LIKE %?2% AND c.name LIKE %?3% AND CONCAT(c.lessonsAmount, '') LIKE %?4% " +
            "AND CONCAT(a.attendedLessonsAmount, '') LIKE %?5% AND CONCAT(a.missedLessonsAmount, '') LIKE %?6%")
    Page<StudentRatingDTO> findAllByPredicate(String name, String surname, String courseName,
                                                    String totalLessons, String attendedLessons, String missedLessons,
                                                    Pageable pageable);

}
