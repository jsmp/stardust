package stardust;

import stardust.handler.FileHandler;
import stardust.handler.StatusHandler;
import stardust.http.Server;

/**
 * Sample HTTP Server
 * @author joao.sampaio@me.com
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {                
        Server httpServer = new Server();
        
        httpServer.getRouter().addRoute("/@status", StatusHandler.class);
        httpServer.getRouter().addRoute(".*$", FileHandler.class);
        
        httpServer.setPort(8080);
        
        try {
            httpServer.serve();
            System.out.println("~ Listening  http://"+httpServer.getDefaultHostName()+":"+httpServer.getPort());
            System.out.println("~ Status avaliable at /@status");
        } catch (Exception ex){
            System.err.println("!! "+ex.getMessage());
        }
    }
}
