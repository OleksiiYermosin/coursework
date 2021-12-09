package ua.training.project.controllers.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.training.project.entities.User;
import ua.training.project.services.UserService;

import javax.validation.Valid;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "/common/login";
    }

    @GetMapping("/register")
    public String getRegistrationPage(){
        return "/common/registration";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute(name = "newUser") User user, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "/common/registration";
        }
        userService.registerUser(user);
        return "redirect:/login";
    }

}
