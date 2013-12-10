package stardust.http;

import java.util.HashMap;
import java.util.Map;
import stardust.handler.utils.HandlerContext;

public class Handler
{
  private static ThreadLocal<Map> context = new ThreadLocal();
  private static ThreadLocal<HandlerContext> handlerContext = new ThreadLocal();

  public static Map<String, Object> getContext()
  {
    if (context.get() == null) context.set(new HashMap());
    return (Map)context.get();
  }

  public static synchronized HandlerContext getHandlerContext()
  {
    if (handlerContext.get() == null) handlerContext.set(new HandlerContext());
    return (HandlerContext)handlerContext.get();
  }

  public static void handle(HttpWorker w, Request rq, Response rs)
  {
    rs.setStatusCode("501 Not Implemented");
  }
}