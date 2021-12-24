package ua.training.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.training.project.controllers.mvc.InstructorController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    private final Logger logger = LoggerFactory.getLogger(InstructorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                logger.error("Resource wasn`t found");
            } else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                logger.error("Internal server error");
            }
        }
        return "common/error";
    }

}
