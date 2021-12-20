package ua.training.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.training.project.entities.Attendance;
import ua.training.project.entities.Course;
import ua.training.project.entities.User;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRatingDTO {

    private User user;

    private Attendance attendance;

    private Course course;

    private BigDecimal averageMark;

    public StudentRatingDTO(User user, Attendance attendance, Course course) {
        this.user = user;
        this.attendance = attendance;
        this.course = course;
    }
}
