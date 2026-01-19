package pl.rentathing.Rental.exception;

public class DateNotAvailableException extends RentalException{
    public DateNotAvailableException() {
        super("Przedmiot nie jest dostępny w wybranym terminie.");
    }
}
