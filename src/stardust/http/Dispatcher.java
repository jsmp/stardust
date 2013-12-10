package stardust.http;

import java.lang.reflect.Method;

public class Dispatcher {
    public static boolean dispatch(HttpWorker w, Request rq, Response rs) {        
        try {
            Class c = w.getServer().getRouter().getHandler(rq.rawRequest);
            if (c != null) {
                w.setHandlerClass(c);
                
                Method handlerMethod = c.getMethod("handle", new Class[]{
                    HttpWorker.class,Request.class,Response.class
                });
                
                handlerMethod.invoke(null, w,rq,rs);                
                return true;
            }
        } catch (Exception ex){}
        return false;
    }
    
}
