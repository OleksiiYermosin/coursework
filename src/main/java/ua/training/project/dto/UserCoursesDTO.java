package ua.training.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.training.project.entities.Course;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoursesDTO {

    private Course course;

    private Long passedAmount;

    private Long failedAmount;

}
