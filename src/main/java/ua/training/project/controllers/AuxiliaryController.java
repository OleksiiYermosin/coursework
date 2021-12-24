package ua.training.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import ua.training.project.controllers.mvc.StudentController;
import ua.training.project.entities.User;
import ua.training.project.exceptions.EntityNotFoundException;
import ua.training.project.services.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

@ControllerAdvice
public class AuxiliaryController {

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(AuxiliaryController.class);

    @Autowired
    public AuxiliaryController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName().equals("anonymousUser") ? null : (User) userService.loadUserByUsername(authentication.getName());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String getErrorPage(EntityNotFoundException exception, Model model){
        logger.error("Exception " + exception.getClass() + " was handled");
        model.addAttribute("user", getUser());
        model.addAttribute("errorMessage", exception.getMessage());
        return "common/errorPage";
    }

    public static Long getCurrentUserId(Model model){
        return ((User) Objects.requireNonNull(model.getAttribute("user"))).getId();
    }

}
