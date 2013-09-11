package fr.an.util.encoder.varlength;

import java.io.IOException;

import fr.an.util.bits.BitInputStream;
import fr.an.util.encoder.huffman.AbstractHuffmanNode;
import fr.an.util.encoder.huffman.HuffmanTable;
import fr.an.util.encoder.huffman.HuffmanTreeLeaf;
import fr.an.util.encoder.huffman.HuffmanTreeNode;

/**
 * 
 */
public class BitInputStreamDecoder {

	private BitInputStream input;
	
	private DivideRounding divideRounding = new DivideRounding();
	
	// ------------------------------------------------------------------------

	public BitInputStreamDecoder(BitInputStream input) {
		super();
		this.input = input;
	}

	// ------------------------------------------------------------------------

	public void setDivideRounding(DivideRounding src) {
		this.divideRounding = new DivideRounding(src);
	}
	
	public DivideRounding getDivideRounding() {
		return divideRounding;
	}

//	public void flush() throws IOException {
//		input.flush();
//	}

	public void close() throws IOException {
		input.close();
		input = null;
	}

	public boolean readBit() {
		try {
			return input.readBit();
		} catch(IOException ioex) {
			throw new RuntimeException(ioex);
		}
	}

	public int readNBits(int bitsLength) {
		try {
			return input.readBits(bitsLength);
		} catch(IOException ioex) {
			throw new RuntimeException(ioex);
		}
	}
	
	
	public int readUInt(int maxValue) {
		int res = 0;
		// recursive divide in approximatly 2 equals part to encode most significant bits first
		// see divideRoundingMode & currentDivideRoundingLower
		// cf corresponding encoder...
		while(maxValue > 1) {
			int maxValueDiv2 = divideRounding.roundDiv2(maxValue);
			boolean mostSignificantBit = readBit();
			if (mostSignificantBit) {
				// value >= maxValueDiv2
				res = res + maxValueDiv2;
				maxValue = maxValue - maxValueDiv2;
			} else {
				// value < maxValueDiv2
				// res .. unchanged
				maxValue = maxValueDiv2;
			}
		}
		return res;
	}
	

	// helper methods
	// ------------------------------------------------------------------------
	
	public int writeUIntAny(int value) {
		return readUInt(Integer.MAX_VALUE);
	}

	public void readNUInts(int[] dest, int maxValue) {
		readNUInts(dest, 0, dest.length, maxValue);
	}
	
	public void readNUInts(int[] dest, int offset, int len, int maxValue) {
		final int maxi = offset + len;
		for (int i = offset; i < maxi; i++) {
			dest[i] = readUInt(maxValue);
		}
	}

	public void readNUInts(int[] dest, int[] valuesWeight) {
		readNUInts(dest, 0, dest.length, valuesWeight);
	}
	
	public void readNUInts(int[] dest, int offset, int len, int[] valuesWeight) {
		final int maxi = offset + len;
		for (int i = offset; i < maxi; i++) {
			dest[i] = readUInt(valuesWeight[i]);
		}
	}
	
	public int[] readNUIntsWithConstrainedSum(int len, int maxSumValueExclusive) {
		int[] tmpres = readNOrderedUInts(len, 0, maxSumValueExclusive);
		// convert from cumulValues[] to values[] 
		int prev = tmpres[0]; 
		for (int i = 1; i < len; i++) {
			int tmp = tmpres[i];
			tmpres[i] = tmpres[i] - prev;
			prev = tmp;
		}
		return tmpres;
	}

//	public void readNUIntsWithConstrainedSum(int[] values, int[] maxEachValue) {
//		int maxCumulValue = maxEachValue[0];
//		for (int i = 1; i < len; i++) {
//			maxCumulValue += maxEachValue[i];
//		}
//		readNOrderedInts(cumulValues, maxCumulValue);
//		int len = values.length;
//		int[] cumulValues = new int[len];
//		cumulValues[0] = values[0]; 
//	}

	public int[] readNOrderedUInts(int len, int maxLastValue) {
		return readNOrderedUInts(len, 0, maxLastValue);
	}
	
	public int[] readNOrderedUInts(int len, int minFirstValue, int maxLastValueExclusive) {
		int[] res = new int[len];
		recReadNOrderedUInts(res, 0, len, minFirstValue, maxLastValueExclusive - 1);
		return res;
	}
	
//	public void readNOrderedUInts(int[] dest, int destOffset, int len, int minFirstValue, int maxLastValue) {
//		recReadNOrderedUInts(0, len, minFirstValue, maxLastValue);
//	}

	private void recReadNOrderedUInts(int[] dest, int fromIndex, int toIndex, int fromMinValue, int toLastValueInclusive) {
		int tmpDiffMax = toLastValueInclusive - fromMinValue + 1;
		if (fromIndex == (toIndex - 1)) {
			int tmpDiff = readUInt(tmpDiffMax);
			dest[fromIndex] = fromMinValue + tmpDiff;
		} else if (fromIndex > toIndex) {
			throw new IllegalArgumentException();
		} else {
			int midIndex = (fromIndex + toIndex) / 2;
			// read mid point, then recurse
			int tmpMidDiff = readUInt(tmpDiffMax);
			int midValue = fromMinValue + tmpMidDiff;
			dest[midIndex] = midValue; 
			// recurse left
			if (fromIndex < midIndex) {
				recReadNOrderedUInts(dest, fromIndex, midIndex, fromMinValue, midValue);
			}
			// recurse right
			if (midIndex+1 < toIndex) {
				recReadNOrderedUInts(dest, midIndex+1, toIndex, midValue, toLastValueInclusive);
			}
		}		
	}

	public HuffmanTreeLeaf readHuffmanSymbol(HuffmanTable table) {
		HuffmanTreeLeaf res = null;
		if (table.getSymbolCount() > 1) {
			HuffmanTreeNode node = table.getRootNode();
			for (;;) {
				HuffmanTreeNode treeNode = (HuffmanTreeNode) node;
				boolean bit = readBit();
				AbstractHuffmanNode child = treeNode.getChildLeftRight(bit);
				if (child.isLeaf()) {
					res = (HuffmanTreeLeaf) child;
					break;
				} else {
					node = (HuffmanTreeNode) child;
				}
			}
		} else {
			// degenerated table: 1 elt
			throw new UnsupportedOperationException(""); // TODO?
		}
		return res;
	}
	
}
