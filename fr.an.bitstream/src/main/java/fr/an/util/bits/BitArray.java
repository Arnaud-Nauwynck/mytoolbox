package fr.an.util.bits;

/**
 * an array of bits
 * 
 * 
 * implementation note:
 * currently using internally  <code>boolean[]</code> 
 * not efficient in memory!!! but first draft easier for debugging
 * single advantage: no need to do 32 or 8 arithmetic, modulo, shifting, downcast, converts 
 * 
 * cf java.nio.ByteBuffer instead?..  
 */
public class BitArray {

	private boolean[] bits;
	
	// ------------------------------------------------------------------------
	
	public BitArray() {
		bits = new boolean[0];
	}

	public BitArray(boolean[] src) {
		bits = new boolean[src.length];
		System.arraycopy(src, 0, bits, 0, src.length);
	}

	// ------------------------------------------------------------------------
	
	public int size() {
		return bits.length;
	}
	
	public boolean[] getBitsCopy() {
		boolean[] res = new boolean[bits.length];
		System.arraycopy(bits, 0, res, 0, bits.length);
		return res;
	}
	
	public void or(BitArray p) {
		int resLen = Math.max(bits.length, p.bits.length);
		boolean[] resBits = new boolean[resLen];
		int minLen = Math.max(bits.length, p.bits.length);
		for (int i = 0; i < minLen; i++) {
			resBits[i] = bits[i] || p.bits[i];
		}
		if (resLen != minLen) {
			boolean[] remainingSrc = (bits.length > p.bits.length) ? bits : p.bits;
			System.arraycopy(remainingSrc, minLen, resBits, minLen, resLen);
		}
		this.bits = resBits;
	}
	
	public void and(BitArray p) {
		int resLen = Math.max(bits.length, p.bits.length);
		boolean[] resBits = new boolean[resLen];
		int minLen = Math.max(bits.length, p.bits.length);
		for (int i = 0; i < minLen; i++) {
			resBits[i] = bits[i] && p.bits[i];
		}
		this.bits = resBits;
	}
	
	public void setAt(int index, boolean value) {
		bits[index] = value;
	}

	public boolean getAt(int index) {
		return bits[index];
	}

	public void shiftLeft(int shiftIndex) {
		if (shiftIndex == 0) {
			return; // do nothing!
		}
		boolean[] resBits;
		int resLen = bits.length + shiftIndex;
		if (resLen < 0) {
			resBits = new boolean[0];
		} else {
			resBits = new boolean[resLen];
			if (shiftIndex > 0) {
				System.arraycopy(bits, 0, resBits, shiftIndex, bits.length);
			} else {
				System.arraycopy(bits, -shiftIndex, resBits, 0, resLen);
			}
		}
		bits = resBits;
	}

	public void shiftRight(int p) {
		shiftLeft(-p);
	}
	
	public void removeNbitsLeft(int truncateLen) {
		int resLen = bits.length - truncateLen;
		boolean[] resBits = new boolean[resLen];
		System.arraycopy(bits, 0, resBits, 0, resLen);
		bits = resBits;
	}
	
	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new BitArray(bits);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof BitArray)) return false;
		BitArray p = (BitArray) obj;
		if (bits.length != p.bits.length) return false;
		boolean res = true;
		int len = bits.length;
		for (int i = 0; i < len; i++) {
			if (bits[i] != p.bits[i]) {
				res = false;
				break;
			}
		}
		return res;
	}

	@Override
	public int hashCode() {
		int res = bits.length;
		int len = bits.length;
		for (int i = 0; i < len; i++) {
			res = res * 31 + ((bits[i])? 1 : 0);
		}
		return res;
	}

	@Override
	public String toString() {
		int len = bits.length;
		StringBuilder sb = new StringBuilder(len);
		for (int i = len-1; i >= 0; i--) {
			boolean b = bits[i];
			sb.append((b) ? '1' : '0');
		}
		return sb.toString();
	}	
	
}
