package fr.an.util.bits;

import java.io.IOException;

/**
 * 
 * 
 * implementation note: using "abstract class" instead of "interface", because 
 * 1) optimisation
 * 2) nobody should use multiple inheritance for this... or use annonymous inner classes?
 * 
 */
public abstract class BitInputStream {

	public abstract void close() throws IOException;

	
	/**
	 * Read a specified number of bits and return them as an int value.
	 */
	public abstract int readBits(int readBitsCounts) throws IOException;

	/**
	 * Read the next bit from the stream.
	 */
	public abstract boolean readBit() throws IOException;
	
}