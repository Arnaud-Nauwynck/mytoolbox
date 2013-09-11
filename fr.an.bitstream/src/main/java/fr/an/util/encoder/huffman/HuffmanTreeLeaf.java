package fr.an.util.encoder.huffman;

/**
 * 
 */
public class HuffmanTreeLeaf extends AbstractHuffmanNode {

	private Object symbol;
	
	private Object userData;

	
	// ------------------------------------------------------------------------

	public HuffmanTreeLeaf(HuffmanTable owner, int frequency, int insertSequenceNumber, Object symbol) {
		super(owner, frequency, insertSequenceNumber);
		this.symbol = symbol;
	}

	// ------------------------------------------------------------------------

	/** Visitor design pattern */
	public void accept(HuffmanNodeVisitor v) {
		v.caseLeaf(this);
	}

	/** Visitor design pattern */
	public <A> void accept(HuffmanNodeVisitor2<A> v, A arg) {
		v.caseLeaf(this, arg);
	}

	public boolean isLeaf() { 
		return true;
	}
	
	public Object getUserData() {
		return userData;
	}

	public void setUserData(Object userData) {
		this.userData = userData;
	}

	public Object getSymbol() {
		return symbol;
	}
	
}
