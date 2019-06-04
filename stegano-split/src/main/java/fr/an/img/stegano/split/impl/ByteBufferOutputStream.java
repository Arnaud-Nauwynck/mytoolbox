package fr.an.img.stegano.split.impl;

import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * missing from jdk?  adapter for java.nio.ByteBuffer to java.io.OutputStream
 */
public class ByteBufferOutputStream extends OutputStream {

    private ByteBuffer byteBuffer;
    
    // ------------------------------------------------------------------------

    public ByteBufferOutputStream (ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    // ------------------------------------------------------------------------

    public ByteBuffer getByteBuffer () {
            return byteBuffer;
    }
    
    public void setByteBuffer (ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
    }
    
    @Override
    public void write (int b) {
        if (!byteBuffer.hasRemaining()) {
            flushRealloc(1);
        }
        byteBuffer.put((byte)b);
    }
    
    @Override
    public void write (byte[] bytes, int offset, int length) {
        if (byteBuffer.remaining() < length) {
            flushRealloc(length - byteBuffer.remaining());
        }
        byteBuffer.put(bytes, offset, length);
    }

    /** to override if you you to write more bytes than buffer can supports */ 
    public void flushRealloc(int more) {
        throw new ArrayIndexOutOfBoundsException();
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "ByteBufferOutputStream [byteBuffer=" + byteBuffer + "]";
    }

}
