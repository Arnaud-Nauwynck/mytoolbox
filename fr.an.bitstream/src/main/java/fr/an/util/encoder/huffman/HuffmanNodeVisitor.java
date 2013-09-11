package fr.an.util.encoder.huffman;

/**
 * Visitor design pattern
 */
public interface HuffmanNodeVisitor {

	public void caseNode(HuffmanTreeNode p);
	
	public void caseLeaf(HuffmanTreeLeaf p);
	
}
