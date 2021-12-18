package ua.training.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.training.project.entities.Event;
import ua.training.project.entities.Mistake;
import ua.training.project.entities.User;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCourseDTO {

    private User user;

    private List<Event> events;

    private List<Mistake> mistakes;

}
