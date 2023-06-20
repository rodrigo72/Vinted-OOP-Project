package Model.Types.Exceptions;

public class CarrierNotFoundException extends Exception {
    public CarrierNotFoundException() {
        super("Carrier not found.");
    }

}
