package stardust.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import stardust.http.Handler;
import stardust.http.Header;
import stardust.http.HttpWorker;
import stardust.http.Request;
import stardust.http.Response;

/**
 *
 * @author joao
 */
public class FileHandler extends Handler {
    public final static File webdir = new File("./web/");
    public static String webDirPath = null;
    
    static {
        try {
            webDirPath = webdir.getCanonicalPath();
        } catch (IOException ex) {}
    }
    
    public static void handle(HttpWorker w, Request rq, Response rs) {
        rs.setStatusCode(Response.NOT_FOUND);
        
        try {
            String path = rq.getURI().getPath();            
            File fPath = new File(webDirPath+File.separator+path);
        
            if (fPath.getCanonicalPath().startsWith(webDirPath) && fPath.isFile()) {
                long length = fPath.length();
                rs.setStatusCode(Response.OK);
                rs.setHeader(Header.CONTENT_LENGTH,Long.toString(length));
                // TODO: CONTENT-TYPE ?
                // TODO: What about ranges?
                rs.sendHeaders();
                
                FileInputStream input = new FileInputStream(fPath);
                FileChannel inChannel = input.getChannel();              
                WritableByteChannel outputChannel = Channels.newChannel(w.getOutput());

                inChannel.transferTo(0, inChannel.size(), outputChannel);
                
                inChannel.close();
                outputChannel.close();
                input.close();
                                
                return;
            }
        } catch (Exception ex) {}
        
        rs.setStatusCode(Response.NOT_FOUND);
        rs.setHeader(Header.CONTENT_TYPE, "text/plain");
        rs.getWriter().println(Response.NOT_FOUND);
    }

}
