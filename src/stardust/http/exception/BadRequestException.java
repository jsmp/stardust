package stardust.http.exception;

/**
 *
 * @author joao
 */
public class BadRequestException extends LazyException {
    
    public BadRequestException(String cause) {
        super(cause);
    }
}