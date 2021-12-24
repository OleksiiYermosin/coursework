package ua.training.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import ua.training.project.entities.Course;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Page<Course> courses;

    private Map<Long, Boolean> userCourseMap;

}
