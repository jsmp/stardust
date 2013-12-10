package stardust.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;
import stardust.http.exception.BadRequestException;

/**
 * Async Socket listener for new connections, socket I/O manager.
 * Incoming data will be sent to a new Request Object for parsing
 * the resulting Request will then be sent to the Dispatcher. (HttpWorker, WebSocketWorker,..)
 *
 * @author joao.sampaio
 */
public class SocketIO extends Thread {
    private Server server;
    private int port;
    private boolean listening = true;
    private int demo=0;

    private static final int BUFFER_SIZE = 16;
    
    public SocketIO(Server server,int port) throws IOException {
            this.server = server;
            this.port = port;
    }
    
    @Override
    public void run() {
        try { 
            this.processs();
        } catch (Exception ex) {}
    }
    
    public Server getServer() {
        return this.server;
    }
    
    public void processs() throws IOException, InterruptedException, BadRequestException {
        Charset charset = Charset.defaultCharset();

        CharsetEncoder encoder = charset.newEncoder();
        CharsetDecoder decoder = charset.newDecoder();

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        Selector selector = Selector.open();

        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new java.net.InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        SelectionKey serverkey = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    
        while (listening) {
            selector.select();
            Set keys = selector.selectedKeys();

            for (Iterator i = keys.iterator(); i.hasNext();) {
                SelectionKey key = (SelectionKey) i.next();
                i.remove();

                if (key==serverkey) {
                    if (key.isAcceptable()) {
                        SocketChannel client = serverSocket.accept();
                        client.configureBlocking(false);
                        SelectionKey clientkey = client.register(selector, SelectionKey.OP_READ);
                        clientkey.attach(new Connection(this,client));
                    }
                } else {
                    SocketChannel client = (SocketChannel) key.channel();
                    if (!key.isReadable())
                        continue;
                    
                    Connection c = (Connection) key.attachment();
                    c.channelReadyForRead();
                    
                    int bytesread = client.read(buffer);
                    if (bytesread == -1) {
                        key.cancel();
                        client.close();
                        continue;
                    }
                    
                    buffer.flip();
                    String request = decoder.decode(buffer).toString();
                    
                    buffer.clear();
                    Connection con = ((Connection) key.attachment());
                    con.read(request);
                    
                  /*)  if (connection.getState() == Request.ParseState.PARSED) {
                        
                        client.write(encoder.encode(CharBuffer.wrap("HTTP/1.0 200 OK\r\nContent-Type: text/plain\r\nServer: "+Server.SERVER_SOFTWARE+"\r\n\r\n"+"404, page not found")));
                        key.cancel();                        
                        client.close();                                        
                    }*/
                    
                }
        }
        }
     }

}