package pl.rentathing.Rental.exception;

public class PastDateException extends RentalException {
    public PastDateException() {
        super("Nie można sprawdzać dostępności w przeszłości");
    }
}
