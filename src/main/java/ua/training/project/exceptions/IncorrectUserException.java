package ua.training.project.exceptions;

public class IncorrectUserException extends EntityNotFoundException{

    public IncorrectUserException(){
        super("exception.user.id");
    }

}
