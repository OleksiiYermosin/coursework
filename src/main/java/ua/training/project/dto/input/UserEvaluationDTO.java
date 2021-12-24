package ua.training.project.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvaluationDTO {

    private Long courseId;

    private Long eventId;

    private List<Long> mistakesList;

}
