package ua.training.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.training.project.entities.Training;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseInfoDTO {

    private Integer lessonsAmount;

    private Integer missedLessonsAmount;

    private Integer attendedLessonsAmount;

    private List<Training> trainings;

    private String courseName;

}
