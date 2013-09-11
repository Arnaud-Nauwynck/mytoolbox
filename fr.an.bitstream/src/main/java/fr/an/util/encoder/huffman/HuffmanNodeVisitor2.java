package fr.an.util.encoder.huffman;

/**
 * Visitor design pattern
 */
public interface HuffmanNodeVisitor2<A> {

	public void caseNode(HuffmanTreeNode p, A arg);
	
	public void caseLeaf(HuffmanTreeLeaf p, A arg);
	
}
