package ua.training.project.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCoursesDTO {

    private Integer pageNumber = 0;

    private String courseName = "";

}
