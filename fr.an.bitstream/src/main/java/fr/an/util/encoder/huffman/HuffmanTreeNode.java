package fr.an.util.encoder.huffman;

/**
 * 
 */
public class HuffmanTreeNode extends AbstractHuffmanNode {

	private AbstractHuffmanNode left;
	private AbstractHuffmanNode right;
	
	// ------------------------------------------------------------------------

	public HuffmanTreeNode(HuffmanTable owner, int seqNumber, AbstractHuffmanNode left, AbstractHuffmanNode right) {
		super(owner, left.frequency + right.frequency, seqNumber);
		this.left = left;
		this.right = right;
		
		left.setParent(this);
		right.setParent(this);
	}
	
	// ------------------------------------------------------------------------

	/** Visitor design pattern */
	public void accept(HuffmanNodeVisitor v) {
		v.caseNode(this);
	}

	/** Visitor design pattern */
	public <A> void accept(HuffmanNodeVisitor2<A> v, A arg) {
		v.caseNode(this, arg);
	}

	public void visitChildren(HuffmanNodeVisitor v) {
		if (left != null) {
			left.accept(v);
		}
		if (right != null) {
			right.accept(v);
		}
	}

	public <A> void visitChildren(HuffmanNodeVisitor2<A> v, A arg) {
		if (left != null) {
			left.accept(v, arg);
		}
		if (right != null) {
			right.accept(v, arg);
		}
	}

	public boolean isLeaf() { 
		return false;
	}

	public AbstractHuffmanNode getLeft() {
		return left;
	}

	public AbstractHuffmanNode getRight() {
		return right;
	}
	
	public AbstractHuffmanNode getChildLeftRight(boolean bit) {
		return (bit)? right : left;
	}
	
}
