package Model.Data.Exceptions;

public class AlreadyExistsInCatalogException extends Exception {
    public AlreadyExistsInCatalogException() {
        super();
    }
    public AlreadyExistsInCatalogException(String message) {
        super(message);
    }
    public AlreadyExistsInCatalogException(String message, Throwable cause) {
        super(message, cause);
    }
    public AlreadyExistsInCatalogException(Throwable cause) {
        super(cause);
    }
}
