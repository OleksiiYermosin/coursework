package ua.training.project.controllers.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.training.project.entities.User;
import ua.training.project.services.CourseService;

import java.util.Objects;

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
        model.addAttribute("courses", courseService.findCoursesByName(name, getCurrentUserId(model)));
        return "student/courses";
    }

    @PostMapping("/enrollUser")
    public String enrollUser(@RequestParam("courseId") Long courseId, Model model){
        courseService.enrollUserInNewCourse(getCurrentUserId(model), courseId);
        return "redirect:/student/myCourses";
    }

    @GetMapping("/myCourses")
    public String getMyCoursesPage(Model model){
        model.addAttribute("courses", courseService.getCoursesAndTrainingsOfUser(getCurrentUserId(model)));
        return "student/coursesUser";
    }

    @GetMapping("/myCourses/{id}")
    public String getCourseInfoPage(@PathVariable("id") Long id, Model model){
        model.addAttribute("courses", courseService.getCourseDescription(getCurrentUserId(model), id));
        return "student/courseInfo";
    }

    @PostMapping("/rateCourse")
    public String rateCourse(@RequestParam("rate") Integer rate, @RequestParam("courseId") Long courseId, Model model){
        courseService.rateCourse(getCurrentUserId(model), courseId, rate);
        return "redirect:/student/myCourses/" + courseId;
    }

    private Long getCurrentUserId(Model model){
        return ((User) Objects.requireNonNull(model.getAttribute("user"))).getId();
    }

}
