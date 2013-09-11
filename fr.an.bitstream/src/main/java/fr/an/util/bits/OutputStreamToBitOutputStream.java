package fr.an.util.bits;

import java.io.IOException;
import java.io.OutputStream;

/**
 * class for writing individual bits from bytes OutputStream
 */
public class OutputStreamToBitOutputStream implements BitOutputStream {
	
	private static final int BITS_MASKS[/*32*/] = {
        0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff,
        0x1ff,0x3ff,0x7ff,0xfff,0x1fff,0x3fff,0x7fff,0xffff,
        0x1ffff,0x3ffff,0x7ffff,0xfffff,0x1fffff,0x3fffff,
        0x7fffff,0xffffff,0x1ffffff,0x3ffffff,0x7ffffff,
        0xfffffff,0x1fffffff,0x3fffffff,0x7fffffff,0xffffffff
    };
	
	/**
	 * The Java OutputStream that is used to write completed bytes.
	 */
	private OutputStream targetOutputStream;
	
	/**
	 * The temporary buffer containing the individual bits until a byte has been
	 * completed and can be commited to the output stream.
	 */
	private int bitsBuffer;
	
	/**
	 * Counts how many bits have been cached up to now.
	 */
	private int bitsBufferLen;
	
	private int debugCountTotalBytes;
	
	// ------------------------------------------------------------------------
	
	public OutputStreamToBitOutputStream(OutputStream aOs) {
		this.targetOutputStream = aOs;
	}

	// ------------------------------------------------------------------------

	private void writeTargetByte(int p) throws IOException {
		targetOutputStream.write((byte) p);
		debugCountTotalBytes++;
	}
	
	/* (non-Javadoc)
	 * @see fr.an.util.bits.BitOutputStream#flush()
	 */
	public void flush() throws IOException {
		if (bitsBufferLen > 0) {
			writeTargetByte(bitsBuffer);
			bitsBufferLen = 0;           
			bitsBuffer = 0;              
		}
	}

	/* (non-Javadoc)
	 * @see fr.an.util.bits.BitOutputStream#close()
	 */
	public void close() throws IOException {
		flush();
		targetOutputStream.close();     
		targetOutputStream = null;
	}

	/* (non-Javadoc)
	 * @see fr.an.util.bits.BitOutputStream#writeBit(boolean)
	 */
	public void writeBit(boolean p) throws IOException {
		writeBits((p)? 1: 0, 1);
	}

	/* (non-Javadoc)
	 * @see fr.an.util.bits.BitOutputStream#writeBits(int, int)
	 */
	 public void writeBits(int bitsValue, int bitsLength) throws IOException {
		bitsValue &= BITS_MASKS[bitsLength];  // only right most bits valid

		if (bitsLength + bitsBufferLen > 8) {
	        while (bitsLength + bitsBufferLen > 8) {
	        	int paddingTo8 = 8 -  bitsBufferLen;
	        	int bitsToComplete = bitsValue >> (bitsLength - paddingTo8);
	        	int valueToFlush = (bitsBuffer << paddingTo8) | bitsToComplete;
	
	        	writeTargetByte(valueToFlush);
	
	            bitsLength -= paddingTo8;
	            bitsValue = bitsValue & BITS_MASKS[bitsLength];
	            bitsBufferLen = 0;
	            bitsBuffer = 0;
	        }
		}
		
        if (bitsLength > 0) {
            bitsBuffer = (bitsBuffer << bitsLength) | bitsValue;
            bitsBufferLen += bitsLength;
        }
        if (bitsBufferLen == 8) {
        	writeTargetByte(bitsBuffer);
        	bitsBufferLen = 0;
            bitsBuffer = 0;
        }
	}

	 
	 public String toString() {
		 return "BitOutputSteam[" 
		 	+ "writtenBytes:" + debugCountTotalBytes 
		 	+ ", bits buffer len:" + bitsBufferLen + " : '" + new Int32BitArray(bitsBuffer, bitsBufferLen).toString() + "'"
		 	+ "]";  
	 }
}
