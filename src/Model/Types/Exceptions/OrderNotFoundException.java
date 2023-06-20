package Model.Types.Exceptions;

public class OrderNotFoundException extends Exception {
    public OrderNotFoundException() {
        super("User not found.");
    }
}
