package ua.training.project.exceptions;

public class IncorrectCourseException extends EntityNotFoundException{

    public IncorrectCourseException() {
        super("exception.course.id");
    }

}
