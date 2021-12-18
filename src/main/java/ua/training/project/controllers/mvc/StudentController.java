package ua.training.project.controllers.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.training.project.controllers.AuxiliaryController;
import ua.training.project.services.CourseService;


@Controller
@RequestMapping("/student")
public class StudentController {

    private final CourseService courseService;

    @Autowired
    public StudentController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("")
    public String getAvailableCoursesPage(@RequestParam(defaultValue = "") String name, Model model){
        model.addAttribute("courses", courseService.findCoursesByName(name, AuxiliaryController.getCurrentUserId(model)));
        return "student/courses";
    }

    @PostMapping("/enrollUser")
    public String enrollUser(@RequestParam("courseId") Long courseId, Model model){
        courseService.enrollUserInNewCourse(AuxiliaryController.getCurrentUserId(model), courseId);
        return "redirect:/student/myCourses";
    }

    @GetMapping("/myCourses")
    public String getMyCoursesPage(Model model){
        model.addAttribute("courses", courseService.getCoursesAndTrainingsOfUser(AuxiliaryController.getCurrentUserId(model)));
        return "student/coursesUser";
    }

    @GetMapping("/myCourses/{id}")
    public String getCourseInfoPage(@PathVariable("id") Long id, Model model){
        model.addAttribute("courses", courseService.getCourseDescription(AuxiliaryController.getCurrentUserId(model), id));
        return "student/courseInfo";
    }

    @PostMapping("/rateCourse")
    public String rateCourse(@RequestParam("rate") Integer rate, @RequestParam("courseId") Long courseId, Model model){
        courseService.rateCourse(AuxiliaryController.getCurrentUserId(model), courseId, rate);
        return "redirect:/student/myCourses/" + courseId;
    }

}
