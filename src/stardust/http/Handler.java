package stardust.http;

import java.util.HashMap;
import java.util.Map;
import stardust.handler.utils.HandlerContext;

/**
 *
 * @author joao
 */
public class Handler {
    private static ThreadLocal<Map> context = new ThreadLocal<Map>();
    private static ThreadLocal<HandlerContext> handlerContext = new ThreadLocal<HandlerContext>();
    
    /**
     * Get the local Thread Context
     * @return a Map containing the local thread Context
     */
    public static Map<String,Object> getContext() {
        if (context.get() == null) context.set(new HashMap<String,Object>());
        return context.get();
    }
    
    /**
     * Get the context attached to the current handler (global for all the threads
     * running this handler)
     * @return a Map containing the handler Context
     */
    public synchronized static HandlerContext getHandlerContext() {
        if (handlerContext.get() == null) handlerContext.set(new HandlerContext());
        return handlerContext.get();
    }             
    
    /**
     * Called by the Worker to give a response to the Client
     * @param w
     * @param rq
     * @param rs 
     */
    public static void handle(HttpWorker w, Request rq, Response rs) {
        rs.setStatusCode(Response.NOT_IMPLEMENTED);        
    }
    
    public void connectionReadyForWrite(Connection connection) {
    
    }
    
    public void connectionReadyForRead(Connection connection) {
    
    
    }
    
    public static boolean isBlocking() {
        return true;
    }
    
}
