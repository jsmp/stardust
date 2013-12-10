package stardust.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.InetAddress;

public class HttpWorker implements Runnable {
    protected Socket socket;
    protected Request request;
    protected Response response;

    private BufferedOutputStream binaryOutput;
    private BufferedReader reader;
    private final Server server;
    private Class handlerClass;

    public HttpWorker(Server server, Socket socket) {
        this.socket = socket;
        this.server = server;
    }
    
    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public Server getServer() {
        return server;
    }

    public void run() {
        try {
            binaryOutput = new BufferedOutputStream(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e){
            return;
        }
      //  request  = new Request(this);
        response = new Response(binaryOutput);

        if (request.getMethod() != null) {
            shutdown();
            return;
        }
     /*   
        try {
            request.parse(reader);
        } catch (BadRequestException ex) {
            response.setStatusCode(Response.BAD_REQUEST);
            response.getWriter().println();
        } catch (LengthRequiredException ex) {
            response.setStatusCode(Response.LENGTH_REQUIRED);
            response.getWriter().println("Length is required");
        } catch (Exception ex) {
            shutdown();
            return;
        }
*/
        if (!Dispatcher.dispatch(this,request, response)) {
            response.setStatusCode(Response.NOT_FOUND);
            response.setHeader(Header.CONTENT_TYPE,"text/plain");
            response.getWriter().println("The Resource you requested was not found.");
        }
        
        shutdown();
        
    }
    
    public OutputStream getOutput() throws IOException {
        return socket.getOutputStream();
    }

    public void shutdown() {
        try {
            if (!response.headersSent()) {
                response.sendHeaders();
            }
            if (response.usedWriter()) {
                response.getWriter().flush();
            }
            binaryOutput.close();
            socket.close();
        } catch (Exception e){}
    }
    
    public void setHandlerClass(Class c) {
        handlerClass =c;
    }
    
    public Class getHandlerClass() {
        return handlerClass;
    }
    

}
