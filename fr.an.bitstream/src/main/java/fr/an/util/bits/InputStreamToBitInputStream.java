package fr.an.util.bits;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;



/**
 * class for reading individual bits from a general Java InputStream.
 *
 */
public class InputStreamToBitInputStream extends BitInputStream {

	/**
	 * The Java InputStream this class is working on.
	 */
	private InputStream targetInputStream;

	/**
	 * 
	 */
	private int bitsBuffer;
	
	/**
	 * 
	 */
	private int bitsBufferLen;
	
	
	private static final int BITS_MASKS[/*32*/] = {
        0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff,
        0x1ff,0x3ff,0x7ff,0xfff,0x1fff,0x3fff,0x7fff,0xffff,
        0x1ffff,0x3ffff,0x7ffff,0xfffff,0x1fffff,0x3fffff,
        0x7fffff,0xffffff,0x1ffffff,0x3ffffff,0x7ffffff,
        0xfffffff,0x1fffffff,0x3fffffff,0x7fffffff,0xffffffff
    };
	
	// ------------------------------------------------------------------------
	
	public InputStreamToBitInputStream(InputStream p) {
		targetInputStream = p;
	}

	// ------------------------------------------------------------------------

	public void close() throws IOException {
		targetInputStream.close();     
		targetInputStream = null;      
	}

	private int readTargetByte() throws IOException {
		return targetInputStream.read();
	}
	
	/**
	 * Read a specified number of bits and return them as an int value.
	 */
	public int readBits(int readBitsCounts) throws IOException {
		int res = 0;
		
		while (readBitsCounts > bitsBufferLen){
            res |= ( bitsBuffer << (readBitsCounts - bitsBufferLen) );
            readBitsCounts -= bitsBufferLen;
            bitsBuffer = readTargetByte();
            if (bitsBuffer == -1) {
                throw new EOFException();
            }
            bitsBufferLen = 8;
        }

        if (readBitsCounts > 0){
            res |= bitsBuffer >> (bitsBufferLen - readBitsCounts);
            bitsBuffer &= BITS_MASKS[bitsBufferLen - readBitsCounts];
            bitsBufferLen -= readBitsCounts;
        }
        
		return res;
		
	}
    
//	/**
//	 * Read the next bit from the stream.
//	 */
//	public boolean readBit() throws IOException {
//		if (bitsBufferLen == 0) {
//			bitsBuffer = readTargetByte();
//			if (bitsBuffer == -1)
//				throw new EOFException();
//			bitsBufferLen = 8;            
//		}
//		int bit = bitsBuffer & (1 << bitsBufferLen);
//		bitsBufferLen++;
//		return  (bit != 0);
//	}
	
	/**
	 * Read the next bit from the stream.
	 */
	public boolean readBit() throws IOException {
		int tmp = readBits(1);
		return (tmp != 0);
	}
	
}

