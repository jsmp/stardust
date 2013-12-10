package stardust.http;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import stardust.http.exception.BadRequestException;

/**
 *
 * @author joao
 */
public class Request {
    protected URI uri;
    protected String method;
    protected String rawRequest;
    protected SocketChannel channel;
    protected SocketIO socketIO;
    
    public static final String GET  = "GET";
    public static final String POST = "POST";
    public static final String HEAD = "HEAD";
    public static final String EOF = "\r\n";
    
    public enum ParseState {REQUEST, HEADER, DATA, PARSED}
    private ParseState state = ParseState.REQUEST;        
    
    private Map<String,Header> headers = new LinkedHashMap<String,Header>(6);
    private Map<String,String> get  = new LinkedHashMap<String,String>();
    private Map<String,String> post = new LinkedHashMap<String,String>();
       
    private StringBuilder buffer = new StringBuilder();    
  
    public Request(SocketIO sio, SocketChannel channel) {
        this.socketIO = sio;
        this.channel = channel;        
    }
    
    public SocketChannel getChannel() {
        return channel;
    }    
    
    public boolean read(String data) throws BadRequestException {
        buffer.append(data);
        if (data.length() == 0) return true;
        
        String line = getLine(buffer.toString());        
        buffer.delete(0, line.length());
        
        if (line.length() == 0) return true;
        System.out.print(":"+line+"");
        
            switch (state) {
                case REQUEST:
                    if (line.startsWith(GET))
                        method = GET;
                    else
                    if (line.startsWith(POST))
                        method = POST;
                    else
                    if (line.startsWith(HEAD))
                        method = HEAD;
                    else
                        throw new BadRequestException("Method not supported");
                
                    rawRequest = line.substring(method.length(),line.lastIndexOf(" "));
                    System.out.println("Request"+rawRequest);
                    state = ParseState.HEADER;
                    return true;
                case HEADER:
                    if (buffer.indexOf(EOF)==0) {
                        state = ParseState.PARSED;
                    }
                    else 
                        if (line.indexOf(":") != -1) {
                            String hname  = line.substring(0, line.indexOf(":"));
                            String hvalue = line.substring(hname.length()).trim();
                            setHeader(hname.trim(),hvalue);
                        } else
                            throw new BadRequestException("Mallformated headers.");
                    
                    return true;
                case DATA:
                    System.out.println("DATA");
                    return true;
            }
        
        return false;
    }    
    
    public static String getLine(String data) {        
        for (int i = 0; i< data.length()-2 ; i++) {
            if (data.charAt(i) == '\r' && data.charAt(i+1) == '\n') {
                return data.substring(0, i+2);
            }
        }        
        return "";
    }
    
    public ParseState getState() {
        return state;
    }
    
    private Map<String,String> decodeParams(String urlencode) throws BadRequestException {
        HashMap<String,String> keyValue = new LinkedHashMap<String,String>();
        if (urlencode == null) return keyValue;

        String[] pairs = urlencode.split("&");

        if (pairs.length > 0) {
            for (String pair : pairs) {
                String[] parts = pair.split("=");
                try {
                    if (parts.length == 1) {
                        keyValue.put(URLDecoder.decode(parts[0],"UTF-8"),"");
                    } else if (parts.length == 2) {
                        keyValue.put(URLDecoder.decode(parts[0],"UTF-8"),
                                     URLDecoder.decode(parts[1],"UTF-8"));
                    }
               } catch(Exception e) {
                   throw new BadRequestException("Invalid urlencode");
               }
            }
        }
        return keyValue;
    }

    private void resolveURI() {
        String schema = "http://";        
        String localHost = socketIO.getServer().getDefaultHostName();

        if (getHeader("host") != null) {
            localHost = getHeader("host").getStringValue();
        }        
        uri = URI.create(schema+localHost+rawRequest);        
    }

    public void setHeader(String name, String value) {
        headers.put(name.toLowerCase(), new Header(name,value));
    }

    public Header getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public String getMethod() {
        return method;
    }

    public URI getURI() {
        return uri;
    }
    
    public String getPost(String key) {
        return post.get(key);
    }
    
    public Map<String,String> getPosts() {
        return post;
    }
    
    public String getGet(String key) {
        return get.get(key);
    }

    public Map<String,String> getGets() {
        return get;
    }

}