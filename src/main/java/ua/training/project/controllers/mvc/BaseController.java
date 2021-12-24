package ua.training.project.controllers.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BaseController {

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    public BaseController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getHomePage(){
        logger.info("Retrieving home page");
        return "/common/index";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        logger.info("Retrieving login page");
        return "/common/login";
    }

    @GetMapping("/register")
    public String getRegistrationPage(){
        logger.info("Retrieving registration page");
        return "/common/registration";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute(name = "newUser") User user, BindingResult bindingResult){
        logger.info("Attempting to register new user: " + user);
        if (bindingResult.hasErrors()){
            logger.warn("Validation process was failed");
            return "/common/registration";
        }
        userService.registerUser(user);
        logger.info("User has been successfully registered");
        return "redirect:/login";
    }

    @ModelAttribute("newUser")
    public User getUser(){
        return new User();
    }

}
