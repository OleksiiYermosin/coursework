package ua.training.project.controllers.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.training.project.repositories.CourseRepository;
@Controller
@RequestMapping("/student")
public class StudentController {

    private final CourseRepository courseRepository;

    @Autowired
    public StudentController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping("")
    public String getAvailableCoursesPage(@RequestParam(defaultValue = "") String name, Model model){
        model.addAttribute("courses", courseRepository.findByNameContaining(name));
        return "student/courses";
    }

}
