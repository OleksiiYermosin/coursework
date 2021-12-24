package ua.training.project.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MistakeSearchDTO {

    private String mistakeName = "";

    private String sort = "name";

    private String sortDirection = "ASC";

    private Integer pageNumber = 0;

}
