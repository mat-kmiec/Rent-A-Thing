package pl.rentathing.auth.exception;

public class UnautorizedException extends AuthException{
    public UnautorizedException() {
        super("Wymagane zalogowanie!");
    }
}
