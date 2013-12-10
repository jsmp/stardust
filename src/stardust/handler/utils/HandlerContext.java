package stardust.handler.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import stardust.http.Handler;

/**
 *
 * @author joao
 */
public class HandlerContext extends SecurityManager {
    private static final Map<Class,Map> handlerContexts = new HashMap<Class,Map>();
    
    private Class handlerClass = Handler.class;    
    
    public HandlerContext() {
        Class[] context = this.getClassContext();        
        
        for (int i = context.length-1; i != 0 ; i--) {
            if (Handler.class.isAssignableFrom(context[i])) {
                handlerClass = context[i];
                break;
            }
        }        
        synchronized(handlerContexts) {
            if (!handlerContexts.containsKey(handlerClass)) {
                handlerContexts.put(handlerClass, new HashMap<String,Object>());
            }
        }        
    }
    
    public Class getHandler() {
        return handlerClass;
    }
    
    public void put(String key, Object value) {
        synchronized(handlerContexts) {
            handlerContexts.get(this.handlerClass).put(key, value);
        }        
    }
    
    public Object get(String key) {
        synchronized(handlerContexts) {
            return handlerContexts.get(this.handlerClass).get(key);
        }
    }
    
    public boolean containsKey(String key) {
        synchronized(handlerContexts) {
            return handlerContexts.get(this.handlerClass).containsKey(key);
        }
    }
    
    public Set keySet(String key) {
        synchronized(handlerContexts) {
            return handlerContexts.get(this.handlerClass).keySet();
        }
    }
        
}
