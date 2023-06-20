package Model.Types.Exceptions;

public class ArticleNegativeNumberException extends ArticleException {
    public ArticleNegativeNumberException() {
        super("Negative number not allowed for prices or time");
    }
    public ArticleNegativeNumberException(String message) {
        super(message);
    }
    public ArticleNegativeNumberException(String message, Throwable cause) {
        super(message, cause);
    }
    public ArticleNegativeNumberException(Throwable cause) {
        super(cause);
    }
}
