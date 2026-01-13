package pl.rentathing.user.exception;

public class UserAlreadyExistException extends UserException{
    public UserAlreadyExistException(String email) {
        super("User with email: " + email + " already exist");
    }
}
