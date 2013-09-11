package fr.an.util.bits;

import java.io.IOException;

/**
 * 
 */
public interface BitOutputStream {

	/**
	 * Write the current cache to the stream and reset the buffer.
	 */
	public void flush() throws IOException;

	/**
	 * Flush the data and close the underlying output stream.
	 */
	public void close() throws IOException;

	/**
	 * Write a single bit to the stream
	 */
	public void writeBit(boolean p) throws IOException;

	/**
	 * Write the specified number of bits from the int value to the stream.
	 * @param bitsFlag the int containing the bits that should be written to the stream.
	 * @param numBits how many bits of the integer should be written to the stream.
	 */
	public void writeBits(int bitsValue, int bitsLength) throws IOException;

}