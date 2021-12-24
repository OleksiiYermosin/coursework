package ua.training.project.controllers.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.training.project.controllers.AuxiliaryController;
import ua.training.project.dto.input.CourseSearchDTO;
import ua.training.project.dto.input.StudentCoursesDTO;
import ua.training.project.exceptions.NotEnoughMoneyException;
import ua.training.project.services.CourseService;
import ua.training.project.services.UserService;

import java.math.BigDecimal;


@Controller
@RequestMapping("/student")
@SessionAttributes("courseSearchDTO")
public class StudentController {

    private final CourseService courseService;

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    public StudentController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("")
    public String getAvailableCoursesPage(@ModelAttribute("courseSearchDTO") CourseSearchDTO courseSearchDTO, Model model){
        logger.info("Executing course search with request params " + courseSearchDTO);
        model.addAttribute("courses", courseService.findCoursesByName(courseSearchDTO,
                AuxiliaryController.getCurrentUserId(model)));
        return "student/courses";
    }

    @PostMapping("/enrollUser")
    public String enrollUser(@RequestParam("courseId") Long courseId, Model model){
        logger.info("Enrolling user to course with id = " + courseId);
        courseService.enrollUserInNewCourse(AuxiliaryController.getCurrentUserId(model), courseId);
        logger.info("Enrolling has been successfully completed");
        return "redirect:/student/myCourses";
    }

    @GetMapping("/myCourses")
    public String getMyCoursesPage(@ModelAttribute("studentCoursesDTO") StudentCoursesDTO studentCoursesDTO, Model model){
        logger.info("Executing course search with request params " + studentCoursesDTO);
        model.addAttribute("courses", courseService.getCoursesAndTrainingsOfUser(
                AuxiliaryController.getCurrentUserId(model), studentCoursesDTO));
        return "student/coursesUser";
    }

    @GetMapping("/myCourses/{id}")
    public String getCourseInfoPage(@PathVariable("id") Long id, Model model){
        logger.info("Retrieving information about course with id = " + id);
        model.addAttribute("courses", courseService.getCourseDescription(AuxiliaryController.getCurrentUserId(model), id));
        logger.info("Information about course has been successfully retrieved");
        return "student/courseInfo";
    }

    @PostMapping("/rateCourse")
    public String rateCourse(@RequestParam("rate") Integer rate, @RequestParam("courseId") Long courseId, Model model){
        logger.info("Attempting to assign rate = " + rate + " to course with id = " + courseId);
        courseService.rateCourse(AuxiliaryController.getCurrentUserId(model), courseId, rate);
        return "redirect:/student/myCourses/" + courseId;
    }

    @GetMapping("/rechargeBalance")
    public String getRechargeBalancePage(){
        logger.info("Retrieving balance recharge page");
        return "student/rechargeBalance";
    }

    @PostMapping("/rechargeBalance")
    public String rechargeBalance(@RequestParam(value = "sum") String sum, Model model){
        logger.info("Attempting to recharge balance");
        if (sum.isEmpty() || BigDecimal.valueOf(Double.parseDouble(sum)).compareTo(BigDecimal.ZERO) <= -1){
            model.addAttribute("error", true);
            logger.warn("Validation process was failed");
            return "student/rechargeBalance";
        }
        userService.updateUserBalance(AuxiliaryController.getCurrentUserId(model), BigDecimal.valueOf(Double.parseDouble(sum)));
        return "redirect:/student/myCourses";
    }

    @ModelAttribute("studentCursesDTO")
    public StudentCoursesDTO getStudentCoursesDTO(){
        return new StudentCoursesDTO();
    }

    @ModelAttribute("courseSearchDTO")
    public CourseSearchDTO getCourseSearchDTO(){
        return new CourseSearchDTO();
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public String handleConstraintViolationException(RedirectAttributes redirectAttributes){
        logger.error("User doesn`t have enough money to enroll into new course");
        redirectAttributes.addFlashAttribute("exception", true);
        return "redirect:/student/rechargeBalance";
    }

}
