package stardust.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 *
 * @author joao
 */
public class Connection {
    protected HttpWorker worker;    
    protected SocketChannel channel;
    protected Request request;
    protected Response response;
    
    protected StringBuilder bufferOut;
    protected StringBuilder bufferIn;
    
    public static final int BUFFER_SIZE = 36;

    protected CharsetEncoder encoder;
    protected CharsetDecoder decoder;
    protected ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    void read(String request) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public enum ConnectionState {ACCEPT, HEADERS, DATA, PROTOCOL, RESPONDING};
    protected ConnectionState state = ConnectionState.ACCEPT;
    
    public Connection(SocketIO sio, SocketChannel channel) {
        this.request = new Request(sio,channel);
        
        Charset charset = Charset.defaultCharset();        
        encoder = charset.newEncoder();
        decoder = charset.newDecoder();                
    }
    
    public SocketChannel getChannel() {
        return channel;
    }
        
    public void channelReadyForRead() throws IOException {
        channel.read(buffer);
        buffer.flip();        
        bufferIn.append(decoder.decode(buffer).toString());
        buffer.clear();
    }
    
    public void channelReadyForWrite() {
    
    }
        
    public void dispatch() {
    
    }    
}