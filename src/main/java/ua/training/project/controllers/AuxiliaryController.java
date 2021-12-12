package ua.training.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import ua.training.project.entities.User;
import ua.training.project.exceptions.IncorrectCourseException;
import ua.training.project.services.UserService;

@ControllerAdvice
public class AuxiliaryController {

    private final UserService userService;

    @Autowired
    public AuxiliaryController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName().equals("anonymousUser") ? null : (User) userService.loadUserByUsername(authentication.getName());
    }

    @ExceptionHandler(IncorrectCourseException.class)
    public String getErrorPage(IncorrectCourseException exception){
        System.out.println(exception.getMessage());
        return "common/index";
    }

}
