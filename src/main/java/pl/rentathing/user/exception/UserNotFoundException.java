package pl.rentathing.user.exception;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String email) {
        super("Użytkownik o emailu: " + email + " nie został znaleziony.");
    }

    public UserNotFoundException(Long id) {
        super("Użytkownik o ID: " + id + " nie został znaleziony.");
    }

    public UserNotFoundException(String message, boolean isCustomMessage) {
        super(message);
    }
}
