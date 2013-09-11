package fr.an.util.bits;


/**
 * implementation of BitOutputStream for counting bits
 * (no IO writes are involved!)
 */
public class CounterBitOuputStream implements BitOutputStream {

	public int count;
	
	// ------------------------------------------------------------------------
	
	public CounterBitOuputStream() {
	}
	
	// ------------------------------------------------------------------------

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void incrCount(int p) {
		this.count += p;
	}
	
	// implements BitOutputStream
	// ------------------------------------------------------------------------
	
	@Override
	public void close() {
		// do nothing
	}

	@Override
	public void flush() {
		// do nothing		
	}

	@Override
	public void writeBit(boolean p) {
		incrCount(1);
	}

	@Override
	public void writeBits(int bitsValue, int bitsLength) {
		incrCount(bitsLength);
	}

	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "CounterBitOutputStream[" + count + "]";
	}
	
}
