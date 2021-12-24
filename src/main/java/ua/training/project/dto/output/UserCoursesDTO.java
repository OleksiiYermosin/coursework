package ua.training.project.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import ua.training.project.entities.Course;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoursesDTO {

    private Page<Course> course;

    private Map<Long, Long> passedEventsMap;

    private Map<Long, Long> failedEventsMap;

}
