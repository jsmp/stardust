package stardust.http;

import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.util.Locale;
import stardust.http.exception.HeadersAlreadySentException;

/**
 *
 * @author joao
 */
public class HttpPrintWriter extends PrintWriter {
    private final Response response;
    private boolean ignoreHeadersAlreadySent = false;

    HttpPrintWriter(BufferedOutputStream out, Response r) {
        super(out);
        response = r;
    }

    public void setIgnoreHeadersAlreadySent(boolean x) {
        ignoreHeadersAlreadySent = x;
    }

    private void verifyHeaders() {
        if (ignoreHeadersAlreadySent) return;
        
        if (response != null && response.headersSent() == false) {
            try {
                response.sendHeaders();
            } catch (HeadersAlreadySentException ex) {}
        }
    }

    @Override
    public void write(int c) {
        verifyHeaders();
        super.write(c);
    }

    @Override
    public void write(char[] buf, int off, int len) {
        verifyHeaders();
        write(buf,off,len);
    }

    @Override
    public void write(char[] buf) {
        verifyHeaders();
        super.write(buf);
    }

    @Override
    public void write(String s, int off, int len) {
        verifyHeaders();
        super.write(s,off,len);
    }

    @Override
    public void write(String s) {
        verifyHeaders();
        super.write(s);
    }


    @Override
    public void print(boolean b) {
        verifyHeaders();
        super.print(b);
    }

    @Override
    public void print(char c) {
        verifyHeaders();
        super.print(c);
    }

    @Override
    public void print(int i) {
        verifyHeaders();
        super.print(i);
    }

    @Override
    public void print(long l) {
        verifyHeaders();
        super.print(l);
    }

    @Override
    public void print(float f) {
        verifyHeaders();
        super.print(f);
    }

    @Override
    public void print(double d) {
        verifyHeaders();
        super.print(d);
    }

    @Override
    public void print(char[] s) {
        verifyHeaders();
        super.print(s);
    }

    @Override
    public void print(String s) {
        verifyHeaders();
        super.print(s);
    }

    @Override
    public void print(Object obj) {
        verifyHeaders();
        super.print(obj);
    }

    @Override
    public void println() {
        verifyHeaders();
        super.println();
    }

    @Override
    public void println(boolean x) {
        verifyHeaders();
        super.println(x);
    }

    @Override
    public void println(char x) {
        verifyHeaders();
        super.println(x);
    }

    @Override
    public void println(int x) {
        verifyHeaders();
        super.println(x);
    }

    @Override
    public void println(long x) {
        verifyHeaders();
        super.println(x);
    }

    @Override
    public void println(float x) {
        verifyHeaders();
        super.println(x);
    }

    @Override
    public void println(double x) {
        verifyHeaders();
        super.println(x);
    }

    @Override
    public void println(char[] x) {
        verifyHeaders();
        super.println(x);
    }

    @Override
    public void println(String x) {
        verifyHeaders();
        super.println(x);
    }

    @Override
    public void println(Object x) {
        verifyHeaders();
        super.println(x);
    }

    @Override
    public HttpPrintWriter printf(String format, Object[] args) {
        verifyHeaders();
        super.printf(format,args);
        return this;
    }

    @Override
    public HttpPrintWriter printf(Locale l, String format, Object[] args) {
        verifyHeaders();
        super.printf(l,format,args);
        return this;
    }

    @Override
    public HttpPrintWriter format(String format, Object[] args) {
        verifyHeaders();
        super.format(format,args);
        return this;
    }

    @Override
    public HttpPrintWriter format(Locale l, String format, Object[] args) {
        verifyHeaders();
        super.format(l,format,args);
        return this;
    }

    @Override
    public HttpPrintWriter append(CharSequence csq) {
        verifyHeaders();
        super.append(csq);
        return this;
    }

    @Override
    public HttpPrintWriter append(CharSequence csq, int start, int end) {
        verifyHeaders();
        super.append(csq,start,end);
        return this;
    }

    @Override
    public HttpPrintWriter append(char c) {
        verifyHeaders();
        super.append(c);
        return this;
    }
}
