package stardust.http;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author joao
 */
public class ConnectionPoolExecutor extends ThreadPoolExecutor {
    public ConnectionPoolExecutor(Server server, int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit,workQueue,
             Executors.defaultThreadFactory());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

        if (r instanceof HttpWorker) {
            HttpWorker worker = (HttpWorker)r;
            if (worker.getHandlerClass() != null) {
                String DATE_FORMAT = "yyyy/MM/dd hh:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                System.out.print(sdf.format(new Date())+", "+worker.getInetAddress()+" "+worker.getHandlerClass().getSimpleName()+"["+worker.response.getStatusCode()+"]: ");
                System.out.print(worker.request.method+" ");
                System.out.println(worker.request.uri);
            }
        }

    }
}
