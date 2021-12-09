package ua.training.project.controllers.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.training.project.entities.User;
import ua.training.project.repositories.CourseRepository;
import ua.training.project.services.UserService;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final CourseRepository courseRepository;

    private final UserService userService;

    @Autowired
    public StudentController(CourseRepository courseRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    @GetMapping("")
    public String getHomePage(@RequestParam(defaultValue = "") String name, Model model){
        model.addAttribute("courses", courseRepository.findByNameContaining(name));
        return "index";
    }

    @ModelAttribute("user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) userService.loadUserByUsername(authentication.getName());
    }

}
