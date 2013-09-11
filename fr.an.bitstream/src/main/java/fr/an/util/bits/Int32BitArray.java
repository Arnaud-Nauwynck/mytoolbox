package fr.an.util.bits;

/**
 * helper class for "bit[N]" where N<=32
 * 
 */
public class Int32BitArray {

	private int bits;
	private int bitsCount;
	
	// ------------------------------------------------------------------------
	
	public Int32BitArray() {
	}

	public Int32BitArray(int bits, int bitCount) {
		this.bits = bits;
		this.bitsCount = bitCount;
	}

	// ------------------------------------------------------------------------
	
	public void or(Int32BitArray p) {
		this.bitsCount = Math.max(bitsCount, p.bitsCount);
		this.bits = bits | p.bits;
	}
	
	public void set(int index) {
		this.bits = (bits | (1 << index));
		this.bitsCount = Math.max(bitsCount, index);
	}

	public boolean get(int index) {
		return (bits & (1 << index)) != 0;
	}

	public void shift(int shiftIndex) {
		this.bits = bits << shiftIndex;
		this.bitsCount += shiftIndex;
	}

	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Int32BitArray(bits, bitsCount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Int32BitArray)) return false;
		Int32BitArray p = (Int32BitArray) obj;
		return bits == p.bits && bitsCount == p.bitsCount;
	}

	@Override
	public int hashCode() {
		return bits ^ bitsCount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(bitsCount);
		for (int i = 0; i < bitsCount; i++) {
			boolean b = ((bits & (1 << i))) != 0;
			sb.append((b) ? '1' : '0');
		}
		return sb.toString();
	}
	
}
