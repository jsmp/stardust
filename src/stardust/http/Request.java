package stardust.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import stardust.http.exception.BadRequestException;
import stardust.http.exception.LengthRequiredException;

public class Request
{
  private final HttpWorker worker;
  protected URI uri;
  protected String method;
  protected String rawRequest;
  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String HEAD = "HEAD";
  private Map<String, Header> headers = new HashMap(6);
  private Map<String, String> get = new HashMap();
  private Map<String, String> post = new HashMap();

  public Request(HttpWorker worker) {
    this.worker = worker;
  }

  public void parse(BufferedReader reader) throws IOException, BadRequestException, LengthRequiredException {
    String requestLine = reader.readLine();
    if (requestLine.trim().equals("")) {
      throw new BadRequestException();
    }

    if (requestLine.startsWith("GET")) {
      this.method = "GET";
    }
    else if (requestLine.startsWith("HEAD")) {
      this.method = "HEAD";
    }
    else if (requestLine.startsWith("POST")) {
      this.method = "POST";
    }
    else {
      throw new BadRequestException();
    }

    String tmp = requestLine.substring(this.method.length() + 1, requestLine.length());
    int indexSP = tmp.indexOf(" ");
    this.rawRequest = (indexSP > 0 ? tmp.substring(0, indexSP) : tmp);

    parseHeaders(reader);
  }

  private void parseHeaders(BufferedReader reader)
    throws BadRequestException, LengthRequiredException
  {
    try
    {
      String inLine = reader.readLine();
      while ((inLine != null) && (!inLine.equals(""))) {
        inLine = inLine.trim();
        String key = inLine.substring(0, inLine.indexOf(":"));
        String value = inLine.substring(inLine.indexOf(":") + 1, inLine.length());

        setHeader(key.trim(), value.trim());
        inLine = reader.readLine();
      }
    } catch (IOException ex) {
      throw new BadRequestException();
    }

    resolveURI();
    resolveParams(reader);
  }

  private Map<String, String> decodeParams(String urlencode) throws BadRequestException {
    HashMap keyValue = new HashMap();
    if (urlencode == null) return keyValue;

    System.out.println("]] Raw Params:" + urlencode);
    String[] pairs = urlencode.split("&");

    if (pairs.length > 0) {
      for (String pair : pairs) {
        String[] parts = pair.split("=");
        try {
          if (parts.length == 1)
            keyValue.put(URLDecoder.decode(parts[0], "UTF-8"), "");
          else if (parts.length == 2)
            keyValue.put(URLDecoder.decode(parts[0], "UTF-8"), URLDecoder.decode(parts[1], "UTF-8"));
        }
        catch (Exception e)
        {
          throw new BadRequestException();
        }
      }
    }
    return keyValue;
  }

  private void resolveParams(BufferedReader reader) throws LengthRequiredException, BadRequestException {
    this.get = decodeParams(getURI().getQuery());

    if (getMethod().equals("POST")) {
      Integer postSize = Integer.valueOf(-1);

      if (getHeader("Content-Type").getStringValue().equals("application/x-www-form-urlencoded"))
        try {
          postSize = Integer.valueOf(Integer.parseInt(getHeader("Content-Length").getStringValue()));
          char[] rawData = new char[postSize.intValue()];
          reader.read(rawData, 0, postSize.intValue());

          this.post = decodeParams(new String(rawData));
        } catch (Exception ex) {
          throw new LengthRequiredException();
        }
    }
  }

  private void resolveURI()
  {
    String schema = this.worker.getServer().getScheme() + "://";

    String localHost = this.worker.getServer().getHostName();

    if (getHeader("host") != null) {
      localHost = getHeader("host").getStringValue();
    }

    this.uri = URI.create(schema + localHost + this.rawRequest);
  }

  public void setHeader(String name, String value) {
    this.headers.put(name.toLowerCase(), new Header(name, value));
  }

  public Header getHeader(String name) {
    return (Header)this.headers.get(name.toLowerCase());
  }

  public String getMethod() {
    return this.method;
  }

  public URI getURI() {
    return this.uri;
  }

  public String getPost(String key) {
    return (String)this.post.get(key);
  }

  public Map<String, String> getPosts() {
    return this.post;
  }

  public String getGet(String key) {
    return (String)this.get.get(key);
  }

  public Map<String, String> getGets() {
    return this.get;
  }

  public Map<String, String> getParams() {
    Map params = new HashMap();
    params.putAll(this.get);
    params.putAll(this.post);

    return params;
  }
}