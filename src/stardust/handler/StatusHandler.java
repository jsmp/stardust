package stardust.handler;

import java.io.PrintWriter;
import java.util.Map;
import stardust.http.Handler;
import stardust.http.Header;
import stardust.http.HttpWorker;
import stardust.http.Request;
import stardust.http.Response;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author joao
 */
public class StatusHandler extends Handler {
    public static void handle(HttpWorker w, Request rq, Response rs) {
        rs.setStatusCode(Response.OK);
        rs.setHeader(Header.CONTENT_TYPE, "text/html");

        PrintWriter out = rs.getWriter();
        Runtime r = Runtime.getRuntime();

        long amemory = r.maxMemory() - r.freeMemory();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Status</title>");
        out.println("<style type=\"text/css\">");
        out.println("body { font-family: Helvetica, Arial, Verdana, sans-serif; font-size:12px}");
        out.println("h1 { font-size:16px } h2 { font-size: 14px;}");
        out.println("table {border-width: 0px;border-spacing: 0px;border-style: none;border-color: gray;border-collapse: collapse;background-color: white;}");
        out.println("table th {border-width: 1px;padding: 4px;border-style: inset;border-color: ;background-color: white;}");
        out.println("table td {border-width: 1px;padding: 4px;border-style: inset;border-color: ;background-color: white;}");
        out.println("</style>");

        out.println("</head>");
        out.println("<body>");
        // Memory
        out.println("<h1>Memory</h1>");
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>Used</th>");
        out.println("<th>Free</th>");
        out.println("<th>Max</th>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td>"+(amemory / 1024 / 1024)+"Mb</td>");
        out.println("<td>"+(r.freeMemory() / 1024 / 1024)+"Mb</td>");
        out.println("<td>"+(r.maxMemory() / 1024 / 1024)+"Mb</td>");
        out.println("</tr>");
        out.println("</table>");

        Map<String,Class> rr = w.getServer().getRouter().getRoutes();
        if (!rr.isEmpty()) {
            out.println("<h1>Router</h1>");
            out.println("<h2>Handlers</h2>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Route</th>");
            out.println("<th>Handler</th>");
            out.println("<th>Package</th>");
            out.println("</tr>");
            
            
            
            for (String ro : rr.keySet()) {
                out.println("<tr>");
                out.println("<td>"+(ro)+"</td>");
                out.println("<td>"+(rr.get(ro).getSimpleName())+"</td>");
                out.println("<td>"+(rr.get(ro).getPackage().getName())+"</td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }
        
        Map<String,String> re = w.getServer().getRouter().getRules();        
        if (!re.isEmpty()) {
            out.println("<h2>Rules</h2>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Route</th>");
            out.println("<th>Handler</th>");
            out.println("</tr>");
            
            for (String ro : re.keySet()) {
                out.println("<tr>");
                out.println("<td>"+(ro)+"</td>");
                out.println("<td>"+(re.get(ro))+"</td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }
    
        out.println("<h1>Running Info</h1>");
        out.println("<table>");
        
        Calendar startDate = Calendar.getInstance();
        Calendar nowDate = Calendar.getInstance();        
        startDate.setTime(w.getServer().getStartedDate());
        
        long mils = nowDate.getTimeInMillis() - startDate.getTimeInMillis();
        long s=mils/1000;
        
        long days    = s / 864000;
        long hours   = (s % 864000) / 3600;
        long minutes = ((s % 864000) % 3600) / 60;
        long seconds = (((s % 864000) % 3600) % 60);
        long milis   = (mils % 1000);
        
        out.println("<tr><td><strong>Running Since</strong></td><td>"+w.getServer().getStartedDate()+"</td></tr>");
        out.println("<tr><td><strong>UpTime</strong></td><td>"
            + (days>0 ? days+" days ": "")
            + (hours>0 ? hours+" hours ": "")
            + (minutes>0 ? minutes+" minutes ": "")
            + (seconds+" seconds ")
            + (milis+" ms ")
            + "</td></tr>");
            
        out.println("<tr><td><strong>Requests</strong></td><td>"+w.getServer().getTotalRequests()+"</td></tr>");
        out.println("</table>");        
        
        out.println("</body>");
        out.println("</html>");
    }
}
