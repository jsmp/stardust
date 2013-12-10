package stardust;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import stardust.handler.FileHandler;
import stardust.handler.StatusHandler;
import stardust.http.Router;
import stardust.http.Server;

public class Main
{
  public static void main(String[] args)
    throws UnsupportedEncodingException, IOException
  {
    Server httpServer = new Server();
    
    System.out.println("~ Listening at http://localhost:8080");
    System.out.println("~ location: @status enabled");
    
    httpServer.getRouter().addRoute("/@status", StatusHandler.class);
    httpServer.getRouter().addRoute(".*$", FileHandler.class);
    httpServer.setPort(8080);

    httpServer.start();
  }
}