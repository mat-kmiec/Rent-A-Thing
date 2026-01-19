package pl.rentathing.item.exception;

public class ItemNotAvailableExpection extends ItemException{
    public ItemNotAvailableExpection(Long itemId) {
        super("Przedmiot o id: " + itemId + "nie jest dostepny");
    }
}
