package stardust.http;

import java.io.IOException;
import java.util.Date;

/**
 * Http server.
 * @author joao.sampaio
 */
public class Server {
    public static final String SERVER_SOFTWARE = "stardust/0.4-prototype";
        
    private Router router = new Router(Router.DEFAULT_HOSTNAME);

    private static int serverPort = 8888;    
    private String defaultServerHostName = "localhost";
    
    private Date startDate = null;
    long totalRequests = 0;
    
    public Router getRouter() {
        return router;
    }

    public String getDefaultHostName() {
        return defaultServerHostName;
    }

    public void setPort(int port) {
        serverPort = port;
    }
    
    public Date getStartedDate() {
        return startDate;
    }
    
    public int getPort() {
        return serverPort;
    }
    
    public long getTotalRequests() {
        return totalRequests;
    }
        
    public void serve() throws IOException {
        SocketIO sIO = new SocketIO(this,getPort());
        sIO.start();
    }
}