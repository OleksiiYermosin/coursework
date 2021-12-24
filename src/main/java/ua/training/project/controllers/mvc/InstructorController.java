package ua.training.project.controllers.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ua.training.project.dto.input.SearchDTO;
import ua.training.project.dto.input.UserEvaluationDTO;
import ua.training.project.dto.input.MistakeSearchDTO;
import ua.training.project.dto.input.StudentCourseInfoSearchDTO;
import ua.training.project.entities.Course;
import ua.training.project.entities.Mistake;
import ua.training.project.services.CourseService;
import ua.training.project.services.InstructorService;
import ua.training.project.services.MistakeService;
import ua.training.project.services.UserService;

import javax.validation.Valid;
import java.util.LinkedList;

@Controller
@RequestMapping("/instructor")
@SessionAttributes({"userEvaluationDTO", "searchDTO"})
public class InstructorController {

    private final UserService userService;

    private final InstructorService instructorService;

    private final CourseService courseService;

    private final MistakeService mistakeService;

    private final Logger logger = LoggerFactory.getLogger(InstructorController.class);

    @Autowired
    public InstructorController(UserService userService, InstructorService instructorService,
                                CourseService courseService, MistakeService mistakeService) {
        this.userService = userService;
        this.instructorService = instructorService;
        this.courseService = courseService;
        this.mistakeService = mistakeService;
    }

    @GetMapping("")
    public String getStudentSearchPage(@RequestParam(defaultValue = "", name = "searchField") String name,
                                       @RequestParam(defaultValue = "0") Integer pageNumber, Model model) {
        logger.info("Retrieving student search page with search name = " + name + " and page = " + pageNumber);
        model.addAttribute("students", userService.findUsersByNameOrSurname(name, pageNumber));
        return "instructor/studentSearch";
    }

    @GetMapping("/user/{id}")
    public String getStudentPage(@PathVariable("id") Long id, Model model) {
        logger.info("Attempting to retrieve user with id = " + id);
        model.addAttribute("userCourseDTO", instructorService.getUserEvaluationInfo(id));
        UserEvaluationDTO userEvaluationDTO;
        if ((userEvaluationDTO = (UserEvaluationDTO) model.getAttribute("userEvaluationDTO")) != null){
            model.addAttribute("mistakeList", mistakeService.getMistakesById(userEvaluationDTO.getMistakesList()));
        }
        logger.info("Information about user with = " + id + "has been successfully retrieved");
        return "instructor/studentInfo";
    }

    @GetMapping("/user/{id}/courses")
    public String getStudentCoursesInfoPage(@PathVariable("id") Long id, @ModelAttribute("studentCourseInfoSearchDTO")
            StudentCourseInfoSearchDTO searchDTO, Model model) {
        logger.info("Attempting to retrieve user`s courses with user id = " + id);
        model.addAttribute("trainings", instructorService.getUserCourses(searchDTO, id));
        model.addAttribute("userId", id);
        logger.info("Information about user`s courses with user id = " + id + "have been successfully retrieved");
        return "instructor/userCoursesInfo";
    }

    @PostMapping("/user/{id}/evaluate")
    public String dispatchForm(@PathVariable("id") Long userId, @ModelAttribute("userEvaluationDTO") UserEvaluationDTO userEvaluationDTO,
                               @RequestParam("action") String actionValue, @RequestParam("mistakeId") Long mistakeId, SessionStatus sessionStatus) {
        logger.info("Dispatching user evaluation request with action " + actionValue);
        if (actionValue.equals("+")) {
            logger.info("Adding new mistake with id = " + mistakeId);
            userEvaluationDTO.getMistakesList().add(mistakeId);
        } else{
            logger.info("Saving user`s training information");
            instructorService.evaluateUser(userId, userEvaluationDTO);
            sessionStatus.setComplete();
        }
        return "redirect:/instructor/user/" + userId;
    }

    @PostMapping("/user/{id}/removeMistake")
    public String removeMistake(@PathVariable("id") Long userId, @RequestParam("mistakeId") Long mistakeId,
                               @ModelAttribute("userEvaluationDTO") UserEvaluationDTO userEvaluationDTO) {
        logger.info("Removing mistake from user`s training with id " + mistakeId);
        userEvaluationDTO.getMistakesList().remove(mistakeId);
        return "redirect:/instructor/user/" + userId;
    }

    @GetMapping("/studentRating")
    public String getStudentRatingPage(@ModelAttribute("searchDTO") SearchDTO searchDTO, Model model){
        logger.info("Retrieving student rating page with params: " + searchDTO);
        model.addAttribute("studentRatingList", instructorService.prepareStudentRatingDTO(searchDTO));
        return "instructor/studentRating";
    }

    @GetMapping("/mistakes")
    public String getMistakesPage(@ModelAttribute("mistakeSearchDTO") MistakeSearchDTO mistakeSearchDTO, Model model){
        logger.info("Retrieving student`s mistakes page with params: " + mistakeSearchDTO);
        model.addAttribute("mistakes", mistakeService.getMistakesByName(mistakeSearchDTO));
        return "instructor/mistakeSearch";
    }

    @GetMapping("/mistake/edit")
    public String getMistakeEditPage(@RequestParam(required = false) Long mistakeId, Model model){
        logger.info("Retrieving mistake`s edit page with id = " + mistakeId);
        if (mistakeId != null) {
            logger.warn("Mistake for editing has been downloaded");
            model.addAttribute("mistake", mistakeService.getMistakeById(mistakeId));
        }
        return "instructor/editMistake";
    }

    @PostMapping("/mistake/edit")
    public String saveMistake(@ModelAttribute("mistake") @Valid Mistake mistake, BindingResult bindingResult, Model model){
        logger.info("Attempting to edit mistake: " + mistake);
        if(bindingResult.hasErrors()){
            logger.warn("Validation process was failed");
            model.addAttribute("mistake", mistake);
            return "instructor/editMistake";
        }
        mistakeService.saveMistake(mistake);
        return "redirect:/instructor/mistakes";
    }

    @GetMapping("/addCourse")
    public String getNewCoursePage(Model model){
        logger.info("Retrieving new course page");
        model.addAttribute("categories", courseService.getCategories());
        return "instructor/addCourse";
    }

    @PostMapping("/addCourse")
    public String addNewCourse(@ModelAttribute("course") @Valid Course course, BindingResult bindingResult,
                               @RequestParam("categoryId") Long categoryId, Model model){
        logger.info("Attempting to add new course");
        if(bindingResult.hasErrors()){
            logger.warn("Validation process was failed");
            model.addAttribute("categories", courseService.getCategories());
            model.addAttribute("course", course);
            return "instructor/addCourse";
        }
        courseService.addNewCourse(course, categoryId);
        logger.info("New course has been successfully added");
        return "redirect:/instructor";
    }

    @ModelAttribute("course")
    public Course getCourse(){
        return new Course();
    }

    @ModelAttribute("mistake")
    public Mistake getMistake(){
        return new Mistake();
    }

    @ModelAttribute("searchDTO")
    public SearchDTO createSearchDTO(){
        return new SearchDTO();
    }

    @ModelAttribute("userEvaluationDTO")
    public UserEvaluationDTO createUserEvaluationDTO() {
        return UserEvaluationDTO.builder().mistakesList(new LinkedList<>()).build();
    }

}
