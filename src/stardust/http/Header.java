package stardust.http;

/**
 *
 * @author joao
 */
public class Header {
    private String name;
    private Object value;

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String HOST = "Host";

    public Header(String name, Object value) {
        this.name = name;
        this.value= value;
    }

    public String getName() {return name;}
    public Object getValue() {return value;}

    public String getStringValue() {
        return value.toString();
    }

    @Override
    public String toString() {
        return name+": "+getStringValue();
    }
    
}