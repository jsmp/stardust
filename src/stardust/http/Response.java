package stardust.http;

import java.io.BufferedOutputStream;
import java.util.HashMap;
import java.util.Map;
import stardust.http.exception.HeadersAlreadySentException;

/**
 *
 * @author joao
 */
public class Response {    
    private final BufferedOutputStream binaryOutput;
    private HttpPrintWriter output;

    private boolean headersSent = false;
    private String statusCode = OK;

    protected Map<String,Header> headers = new HashMap<String, Header>(6);

    // Success
    public static final String OK = "200 OK";
    public static final String CREATED = "201 Created";
    public static final String ACCEPTED = "202 Accepted";
    public static final String NON_AUTHORITATIVE = "203 Non-Authoritative Information (since HTTP/1.1)";
    public static final String NO_CONTENT = "204 No Content";
    public static final String RESET_CONTENT = "205 Reset Content";
    public static final String PARTIAL_STATUS = "206 Partial Content";
    public static final String MULTI_STATUS = "207 Multi-Status (WebDAV) (RFC 4918)";

    // Redirection
    public static final String MULTIPLE_CHOICES   = "300 Multiple Choices";
    public static final String MOVED_PERMANENTLY  = "301 Moved Permanently";
    public static final String FOUND  = "302 Found";
    public static final String SEE_OTHER  = "303 See Other (since HTTP/1.1)";
    public static final String NOT_MODIFIED  = "304 Not Modified";
    public static final String USE_PROXY  = "305 Use Proxy (since HTTP/1.1)";
    public static final String SWITCH_PROXY = "306 Switch Proxy";
    public static final String TEMPORARY_REDIRECT = "307 Temporary Redirect (since HTTP/1.1)";

    // Client Error
    public static final String BAD_REQUEST ="400 Bad Request";
    public static final String UNAUTHORIZED ="401 Unauthorized";
    public static final String PAYMENT_REQUIRED ="402 Payment Required";
    public static final String FORBIDDEN ="403 Forbidden";
    public static final String NOT_FOUND ="404 Not Found";
    public static final String METHOD_NOT_ALLOWED ="405 Method Not Allowed";
    public static final String NOT_ACCEPTABLE ="406 Not Acceptable";
    public static final String PROXY_AUTHENTICATION_REQUIRED ="407 Proxy Authentication Required";
    public static final String REQUEST_TIMEOUT ="408 Request Timeout";
    public static final String CONFLIT  ="409 Conflict";
    public static final String GONE ="410 Gone";
    public static final String LENGTH_REQUIRED ="411 Length Required";
    public static final String PRECONDITION_FAILED ="412 Precondition Failed";
    public static final String REQUEST_ENTITY_TOO_LARGE ="413 Request Entity Too Large";
    public static final String REQUEST_URI_TOO_LONG ="414 Request-URI Too Long";
    public static final String UNSUPORTED_MEDIA_TYPE ="415 Unsupported Media Type";
    public static final String REQUESRED_RANGE_NOT_SATISFIABLE ="416 Requested Range Not Satisfiable";
    public static final String EXPECTATION_FAILED ="417 Expectation Failed";
    public static final String IAM_A_TEAPOT ="418 I'm a teapot";
    public static final String TOO_MANY_CONNECTIONS_FROM_YOU ="421 There are too many connections from your internet address";
    public static final String UNPROCESSABLE_ENTITY ="422 Unprocessable Entity (WebDAV) (RFC 4918)";
    public static final String LOCKED ="423 Locked (WebDAV) (RFC 4918)";
    public static final String FAILED_DEPENDENCY ="424 Failed Dependency (WebDAV) (RFC 4918)";
    public static final String UNORDERED_COLLECTION ="425 Unordered Collection (RFC 3648)";
    public static final String UPGRADE_REQUIRED ="426 Upgrade Required (RFC 2817)";
    public static final String RETRY_WITH ="449 Retry With";
    //public static final String BLOCKED_BY_WINDOWS_PARENTAL_CONTROLS ="450 Blocked by Windows Parental Controls";

    // Server Error
    public static final String INTERNAL_SERVER_ERROR = "500 Internal Server Error";
    public static final String NOT_IMPLEMENTED = "501 Not Implemented";
    public static final String BAD_GATEWAY = "502 Bad Gateway";
    public static final String SERVICE_UNAVALIABLE  = "503 Service Unavailable";
    public static final String GATEWAY_TIMEOUT = "504 Gateway Timeout";
    public static final String HTTP_VERSION_NOT_SUPPORTED  = "505 HTTP Version Not Supported";
    public static final String VARIANT_ALSO_NEGOTIATES = "506 Variant Also Negotiates (RFC 2295)";
    public static final String INSUFFICIENT_STORAGE = "507 Insufficient Storage (WebDAV) (RFC 4918)";
    public static final String BANDWIDTH_LIMIT_EXCEEDED  = "509 Bandwidth Limit Exceeded";
    public static final String NOT_EXTENTED = "510 Not Extended (RFC 2774)";
    public static final String USER_ACCESS_DENIED  = "530 User access denied";

    public Response(BufferedOutputStream binaryOutput) {
        this.binaryOutput = binaryOutput;
    }

    public HttpPrintWriter getWriter() {
        if (this.output == null) {
            this.output = new HttpPrintWriter(binaryOutput,this);
        }
        return this.output;
    }

    public boolean usedWriter() {
        return output != null;
    }

    public void setHeader(String name, String value) {
        headers.put(name.toLowerCase(), new Header(name,value));
    }

    public void setHeader(Header h) {
        headers.put(h.getName().toLowerCase(), h);
    }

    public Header getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    boolean headersSent() {
        return headersSent;
    }

    public void sendHeaders() throws HeadersAlreadySentException {
        if (headersSent) {
            throw new HeadersAlreadySentException();
        } else {
            HttpPrintWriter w = this.getWriter();
            w.setIgnoreHeadersAlreadySent(true);
            w.print("HTTP/1.1 ");
            w.print(getStatusCode());
            w.println();

            for (Header h : headers.values()) {
                w.println(h);
            }

            w.print("\r\n");
            w.flush();
            w.setIgnoreHeadersAlreadySent(false);
            headersSent = true;
        }
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    
}
