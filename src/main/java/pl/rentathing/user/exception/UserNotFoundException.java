package pl.rentathing.user.exception;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String email) {
        super("User with email: " + email + " not found");
    }
}
