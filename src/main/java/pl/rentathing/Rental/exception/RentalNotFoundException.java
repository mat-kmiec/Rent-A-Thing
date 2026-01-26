package pl.rentathing.Rental.exception;

public class RentalNotFoundException extends RuntimeException {
    public RentalNotFoundException() {
        super("Nie znaleziono rezerwacji");
    }
}
