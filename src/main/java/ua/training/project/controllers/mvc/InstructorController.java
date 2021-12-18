package ua.training.project.controllers.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ua.training.project.dto.UserEvaluationDTO;
import ua.training.project.services.InstructorService;
import ua.training.project.services.MistakeService;
import ua.training.project.services.UserService;

import java.util.LinkedList;

@Controller
@RequestMapping("/instructor")
@SessionAttributes("userEvaluationDTO")
public class InstructorController {

    private final UserService userService;

    private final InstructorService instructorService;

    private final MistakeService mistakeService;

    @Autowired
    public InstructorController(UserService userService, InstructorService instructorService, MistakeService mistakeService) {
        this.userService = userService;
        this.instructorService = instructorService;
        this.mistakeService = mistakeService;
    }

    @GetMapping("")
    public String getStudentSearchPage(@RequestParam(defaultValue = "", name = "searchField") String name, Model model) {
        model.addAttribute("students", userService.findUsersByNameOrSurname(name));
        return "instructor/studentSearch";
    }

    @GetMapping("/user/{id}")
    public String getStudentPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("userCourseDTO", instructorService.getUserEvaluationInfo(id));
        UserEvaluationDTO userEvaluationDTO;
        if ((userEvaluationDTO = (UserEvaluationDTO) model.getAttribute("userEvaluationDTO")) != null){
            model.addAttribute("mistakeList", mistakeService.getMistakesById(userEvaluationDTO.getMistakesList()));
        }
        return "instructor/studentInfo";
    }

    @PostMapping("/user/{id}/evaluate")
    public String dispatchForm(@PathVariable("id") Long userId, @ModelAttribute("userEvaluationDTO") UserEvaluationDTO userEvaluationDTO,
                               @RequestParam("action") String actionValue, @RequestParam("mistakeId") Long mistakeId, SessionStatus sessionStatus) {
        if (actionValue.equals("+")) {
            userEvaluationDTO.getMistakesList().add(mistakeId);
        } else{
            instructorService.evaluateUser(userId, userEvaluationDTO);
            sessionStatus.setComplete();
        }
        return "redirect:/instructor/user/" + userId;
    }

    @PostMapping("/user/{id}/removeMistake")
    public String removeMistake(@PathVariable("id") Long userId, @RequestParam("mistakeId") Long mistakeId,
                               @ModelAttribute("userEvaluationDTO") UserEvaluationDTO userEvaluationDTO) {
        userEvaluationDTO.getMistakesList().remove(mistakeId);
        return "redirect:/instructor/user/" + userId;
    }

    @ModelAttribute("userEvaluationDTO")
    public UserEvaluationDTO createUserEvaluationDTO() {
        return UserEvaluationDTO.builder().mistakesList(new LinkedList<>()).build();
    }

}
