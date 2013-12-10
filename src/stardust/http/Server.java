package stardust.http;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLServerSocketFactory;

public class Server extends Thread
{
  public static final String SERVER_SOFTWARE = "stardust/0.4-prototype";
  private static final int THREAD_COUNT = 100;
  private static final int THREAD_MAX = 200;
  private static final int THREAD_TIMEOUT = 30;
  private ConnectionPoolExecutor pool;
  private LinkedBlockingQueue queue;
  private ServerSocket serverSocket;
  private Router router = new Router();

  private static int serverPort = 8888;
  private String serverSoftware = "stardust/0.4-prototype";
  private String serverHostName = "localhost";
  private boolean useSSL = false;
  private String scheme = "http";

  private Date startDate = null;
  long totalRequests = 0L;

  public Router getRouter() {
    return this.router;
  }

  public String getHostName() {
    return this.serverHostName;
  }

  public void setSSL(String sslStore, String storePassword) {
    System.setProperty("javax.net.ssl.keyStore", sslStore);
    System.setProperty("javax.net.ssl.keyStorePassword", storePassword);
    this.scheme = "https";
    this.useSSL = true;
  }

  public String getScheme() {
    return this.scheme;
  }

  public void setPort(int port) {
    serverPort = port;
  }

  public Date getStartedDate() {
    return this.startDate;
  }

  public int getPort() {
    return serverPort;
  }

  public long getTotalRequests() {
    return this.totalRequests;
  }

  public void run()
  {
    this.startDate = new Date();
    this.totalRequests = 0L;
    this.queue = new LinkedBlockingQueue();

    this.pool = new ConnectionPoolExecutor(this, 500, 1000, 180L, TimeUnit.SECONDS, this.queue);
    try
    {
      if (this.useSSL) {
        SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

        this.serverSocket = sslserversocketfactory.createServerSocket(serverPort);
      } else {
        this.serverSocket = new ServerSocket(serverPort);
      }

      this.serverSocket.setReuseAddress(true);
      this.serverSocket.setPerformancePreferences(2147483647, 0, 0);

      while (this.serverSocket.isBound())
        try {
          this.pool.execute(new HttpWorker(this, this.serverSocket.accept()));
          this.totalRequests += 1L;
        } catch (IOException ex) {
          ex.printStackTrace();
          System.exit(0);
        }
    }
    catch (IOException ex) {
      this.pool.shutdownNow();
      System.err.println("Error: " + ex.getMessage());
      System.exit(0);
    }
  }
}