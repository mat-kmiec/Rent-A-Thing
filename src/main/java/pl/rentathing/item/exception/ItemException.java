package pl.rentathing.item.exception;


public abstract class ItemException extends RuntimeException {
    public ItemException(String message) {
        super(message);
    }
}
