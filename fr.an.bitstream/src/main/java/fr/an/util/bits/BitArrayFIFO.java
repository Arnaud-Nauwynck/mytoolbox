package fr.an.util.bits;

import java.io.EOFException;
import java.io.IOException;

/**
 * a simple FIFO queue of bit(boolean), with BitInputStream/BitOutputStream end points
 */
public class BitArrayFIFO {

	private BitArray buffer = new BitArray(); 
	
	private BitInputStream inputEndPoint = new InnerBitInputStream();
	private BitOutputStream outputEndPoint = new InnerBitOutputStream();
	
	// ------------------------------------------------------------------------
	
	public BitArrayFIFO() {
	}
	
	// ------------------------------------------------------------------------
	
	public BitArray getBuffer() {
		return buffer;
	}
	
	public BitInputStream getInputEndPoint() {
		return inputEndPoint;
	}

	public BitOutputStream getOutputEndPoint() {
		return outputEndPoint;
	}

	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	public String toString() {
		return "BooleanArrayQueue[" + buffer + "]";
	}
	
	// internal inner classes
	// ------------------------------------------------------------------------
	
	private class InnerBitInputStream extends BitInputStream {

		@Override
		public void close() throws IOException {
			// do nothing?
		}

		@Override
		public boolean readBit() throws IOException {
			int index = buffer.size() - 1;
			if (index < 0) throw new EOFException();
			boolean res = buffer.getAt(index);
			buffer.removeNbitsLeft(1);
			return res;
		}

		@Override
		public int readBits(int readBitsCounts) throws IOException {
			int index = buffer.size() - 1;
			if (index < 0) throw new EOFException();
			int res = 0;
			for (int i = 0; i < readBitsCounts; i++) {
				boolean bit = buffer.getAt(index - i);
				res = (res << 1) | ((bit) ? 1 : 0);
			}
			buffer.removeNbitsLeft(readBitsCounts);
			return res;
		}
		
	}

	private class InnerBitOutputStream implements BitOutputStream {

		@Override
		public void close() throws IOException {
			// do nothing?
		}

		@Override
		public void flush() throws IOException {
			// do nothing?
		}

		@Override
		public void writeBit(boolean p) throws IOException {
			buffer.shiftLeft(1);
			buffer.setAt(0, p);
		}

		@Override
		public void writeBits(int bitsValue, int bitsLength) throws IOException {
			buffer.shiftLeft(bitsLength);
			for (int i = 0; i < bitsLength; i++) {
				boolean bit = 0 != (bitsValue & (1 << i));
				buffer.setAt(i, bit);
			}
		}
		
	}

}
