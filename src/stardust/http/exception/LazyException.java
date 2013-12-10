package stardust.http.exception;

/**
 *
 * @author joao
 */
public class LazyException extends RuntimeException {
  
  public LazyException(){
      super();
  }
    
  public LazyException(String desc) {
      super(desc);
  }
  
  public LazyException(String desc, Throwable cause){
      super(desc, cause);
  }

  public LazyException(Throwable cause){
      super(cause);
  }  
  
  @Override
  public Throwable fillInStackTrace() { // avoid filling in the stack
        return null;
  }
}
