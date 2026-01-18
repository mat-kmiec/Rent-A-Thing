package pl.rentathing.Rental.exception;

public class ItemNotAvailableException extends RentalException{
    public ItemNotAvailableException() {
        super("Przedmiot nie jest dostępny w wybranym terminie.");
    }
}
