package pl.rentathing.auth.exception;

public class UserAlreadyExistException extends AuthException{
    public UserAlreadyExistException(String email) {
        super("User with email: " + email + " already exist");
    }
}
