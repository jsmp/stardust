package stardust.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import stardust.http.exception.BadRequestException;
import stardust.http.exception.LengthRequiredException;

public class HttpWorker
  implements Runnable
{
  protected Socket socket;
  protected Request request;
  protected Response response;
  private BufferedOutputStream binaryOutput;
  private BufferedReader reader;
  private final Server server;
  private Class handlerClass;

  public HttpWorker(Server server, Socket socket)
  {
    this.socket = socket;
    this.server = server;
  }

  public InetAddress getInetAddress() {
    return this.socket.getInetAddress();
  }

  public Server getServer() {
    return this.server;
  }

  public void run() {
    try {
      this.binaryOutput = new BufferedOutputStream(this.socket.getOutputStream());
      this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    } catch (Exception e) {
      return;
    }
    this.request = new Request(this);
    this.response = new Response(this.binaryOutput);

    if (this.request.getMethod() != null) {
      shutdown();
      return;
    }
    try
    {
      this.request.parse(this.reader);
    } catch (BadRequestException ex) {
      this.response.setStatusCode("400 Bad Request");
      this.response.getWriter().println();
    } catch (LengthRequiredException ex) {
      this.response.setStatusCode("411 Length Required");
      this.response.getWriter().println("Length is required");
    } catch (Exception ex) {
      shutdown();
      return;
    }

    if (!Dispatcher.dispatch(this, this.request, this.response)) {
      this.response.setStatusCode("404 Not Found");
      this.response.setHeader("Content-Type", "text/plain");
      this.response.getWriter().println("The Resource you requested was not found.");
    }

    shutdown();
  }

  public OutputStream getOutput() throws IOException
  {
    return this.socket.getOutputStream();
  }

  public void shutdown() {
    try {
      if (!this.response.headersSent()) {
        this.response.sendHeaders();
      }
      if (this.response.usedWriter()) {
        this.response.getWriter().flush();
      }
      this.binaryOutput.close();
      this.socket.close(); } catch (Exception e) {
    }
  }

  public void setHandlerClass(Class c) {
    this.handlerClass = c;
  }

  public Class getHandlerClass() {
    return this.handlerClass;
  }
}