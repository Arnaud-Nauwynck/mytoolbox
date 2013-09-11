package fr.an.util.encoder.huffman;


/**
 * 
 */
public abstract class AbstractHuffmanNode implements Comparable<AbstractHuffmanNode> {

	private HuffmanTable owner;
	
	protected AbstractHuffmanNode parent;
	
	protected int frequency;

	protected int insertSequenceNumber;
	
	protected HuffmanBitsCode resultCode;

	// ------------------------------------------------------------------------

	public AbstractHuffmanNode(HuffmanTable owner, int frequency, int insertSequenceNumber) {
		this.owner = owner;
		this.frequency = frequency;
		this.insertSequenceNumber = insertSequenceNumber;
	}
	
	// ------------------------------------------------------------------------

	/** Visitor design pattern */
	public abstract void accept(HuffmanNodeVisitor v);

	public void visitParent(HuffmanNodeVisitor v) {
		if (parent != null) {
			parent.accept(v);
		}
	}

	/** Visitor design pattern */
	public abstract <A> void accept(HuffmanNodeVisitor2<A> v, A arg);

	public <A> void visitParent(HuffmanNodeVisitor2<A> v, A arg) {
		if (parent != null) {
			parent.accept(v, arg);
		}
	}

	public abstract boolean isLeaf();
	
	public HuffmanTable getOwner() {
		return owner;
	}

	public AbstractHuffmanNode getParent() {
		return parent;
	}

	/*pp*/ void setParent(HuffmanTreeNode p) {
		this.parent = p;
	}

	public int getFrequency() {
		return frequency;
	}

	public HuffmanBitsCode getResultCode() {
		return resultCode;
	}

	/*pp*/ void setResultCode(HuffmanBitsCode p) {
		this.resultCode = p;
	}

	public int getSequenceNumber() {
		return insertSequenceNumber;
	}
	
	// ------------------------------------------------------------------------

	/** implements Comparable */
	public int compareTo(AbstractHuffmanNode p) {
		int res;
		int p_frequency = p.frequency;
		if (frequency < p_frequency) {
			res = -1;
		} else if (frequency > p_frequency) {
			res = +1;
		} else {
			int p_insertSequenceNumber = p.insertSequenceNumber;
			if (insertSequenceNumber < p_insertSequenceNumber) {
				res = +1;
			} else if (insertSequenceNumber > p_insertSequenceNumber) {
				res = -1;
			} else {
				res = 0; // should not occur, except for comparing to itself! (insertSequenceNumber is uniq)
			}
		}
		return res;
	}
}
