package Model.Types.Exceptions;

public class ReturnOrderException extends Exception {
    public ReturnOrderException() {
        super("Cannot return order. Time limit exceeded.");
    }
}
