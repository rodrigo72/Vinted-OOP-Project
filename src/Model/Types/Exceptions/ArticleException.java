package Model.Types.Exceptions;

public class ArticleException extends Exception {
    public ArticleException() {
        super("Article exception.");
    }
    public ArticleException(String message) {
        super(message);
    }
    public ArticleException(Throwable cause) {
        super(cause);
    }
    public ArticleException(String message, Throwable cause) {
        super(message, cause);
    }
}
