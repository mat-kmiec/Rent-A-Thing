package pl.rentathing.Rental.exception;

public class StartAfterEndDateException extends RentalException{
    public StartAfterEndDateException() {
        super("Data zakończenia nie może być wcześniejsza niż data rozpoczęcia.");
    }
}
