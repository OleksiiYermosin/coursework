package ua.training.project.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {

    private String name = "";

    private String surname = "";

    private String courseName = "";

    private String totalLessons = "";

    private String attendedLessons = "";

    private String missedLessons = "";

    private String sort = "user.name:ASC";

    private Integer pageNumber = 0;

}
