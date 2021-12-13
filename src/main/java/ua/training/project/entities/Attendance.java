package ua.training.project.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.training.project.utils.AttendanceKey;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attendances")
public class Attendance {

    @EmbeddedId
    private AttendanceKey id;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private User user;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    private Integer missedLessonsAmount;

    private Integer attendedLessonsAmount;

    private Boolean isPassed = false;

    private Integer rate;

}
