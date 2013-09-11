package fr.an.util.encoder.huffman;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 
 */
public class HuffmanTable {

	
	private Map<Object,HuffmanTreeLeaf> symbolLeafMap = new LinkedHashMap<Object,HuffmanTreeLeaf>();
	
	private List<AbstractHuffmanNode> sortedNodes = new ArrayList<AbstractHuffmanNode>();
	
	/**
	 * intermediate nodes added to build huffman tree
	 */
	private List<HuffmanTreeNode> mergedNodes = new ArrayList<HuffmanTreeNode>();

	// ------------------------------------------------------------------------

	public HuffmanTable() {
	}
	
	// ------------------------------------------------------------------------

	public int getSymbolCount() {
		return symbolLeafMap.size();
	}
	
	public HuffmanTreeLeaf addSymbol(Object symbol, int freq) {
		int seqNumber = symbolLeafMap.size();
		HuffmanTreeLeaf p = new HuffmanTreeLeaf(this, freq, seqNumber, symbol);
		symbolLeafMap.put(symbol, p);
		return p;
	}

	public HuffmanTreeLeaf getSymbolLeaf(Object symbol) {
		return symbolLeafMap.get(symbol);
	}

	public void compute() {
		mergedNodes.clear();
		Queue<AbstractHuffmanNode> queue = new PriorityQueue<AbstractHuffmanNode>(symbolLeafMap.size());
		for(HuffmanTreeLeaf leaf : symbolLeafMap.values()) {
			queue.add(leaf);
		}
		
		int seqNumberGenerator = symbolLeafMap.size();
		while(queue.size() >= 2) {
			AbstractHuffmanNode right = queue.poll();
			AbstractHuffmanNode left = queue.poll();
			// int freq = left.getFrequency() + right.getFrequency();
			int seqNumber = seqNumberGenerator++;
			HuffmanTreeNode newNode = new HuffmanTreeNode(this, seqNumber, left, right);
			queue.add(newNode);
			mergedNodes.add(newNode);
		}
		
		assignCodes();
				
		sortedNodes.clear();
		// TreeSet<AbstractHuffmanNode> tmpSorted = new TreeSet<AbstractHuffmanNode>();
		for(HuffmanTreeLeaf leaf : symbolLeafMap.values()) {
			sortedNodes.add(leaf);
		}
		for(HuffmanTreeNode node : mergedNodes) {
			sortedNodes.add(node);
		}
	}
	
	private void assignCodes() {
		HuffmanTreeNode root = getRootNode();
		HuffmanBitsCode rootCode = HuffmanBitsCode.getRootEmptyCode();
		root.setResultCode(rootCode);
		
		root.accept(new HuffmanNodeVisitor() {
			public void caseNode(HuffmanTreeNode p) {
				HuffmanBitsCode code = p.getResultCode();
				HuffmanBitsCode leftCode = HuffmanBitsCode.getLeftCode(code);
				HuffmanBitsCode rightCode = HuffmanBitsCode.getRightCode(code);
				p.getLeft().setResultCode(leftCode);
				p.getRight().setResultCode(rightCode);
				p.visitChildren(this);
			}
			public void caseLeaf(HuffmanTreeLeaf p) {
				// do nothing
			}
		});
	}

	public HuffmanTreeNode getRootNode() {
		return mergedNodes.get(mergedNodes.size() - 1);
	}

}
