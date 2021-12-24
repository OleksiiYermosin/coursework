package ua.training.project.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseInfoSearchDTO {

    private String courseName = "";

    private String eventName = "";

    private String categoryName = "";

    private String sort = "course.name";

    private String sortDirection = "ASC";

    private Integer pageNumber = 0;

}
