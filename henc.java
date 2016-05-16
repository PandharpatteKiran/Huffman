import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.lang.Comparable;
class Node {
	
	Node() {}  	
}
final class LeafNode extends Node {
	
	public final int sym;

	public LeafNode(int sym) {
		if (sym < 0)
			throw new IllegalArgumentException("Symbol value is Invalid");
		this.sym = sym;
	}
	
}
final class InternalNode extends Node {
	
	public final Node leftChild;
	public final Node rightChild;
	
	public InternalNode(Node leftChild, Node rightChild) {
		if (leftChild == null || rightChild == null)
			throw new NullPointerException("Invalid Symbol !");
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}
}
final class CTree {
	
	public final InternalNode root; 
	//code for each node insert in the tree
	private List<List<Integer>> hcode;
	
	public CTree(InternalNode root, int limit) {
		if (root == null)
			throw new NullPointerException("Only on symbol");
		this.root = root;
		
		//Initial code for each symbol inserted in the tree
		hcode = new ArrayList<List<Integer>>(); 
		
		for (int i = 0; i < limit; i++)
			hcode.add(null);
		//build a list of symbols with respective code
		buildCList(root, new ArrayList<Integer>());
		
	}
	
	private void buildCList(Node node1, List<Integer> prefixcode) {
		
		if (node1 instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node1;
			prefixcode.add(0);
			buildCList(internalNode.leftChild , prefixcode);
			prefixcode.remove(prefixcode.size() - 1);
			
			prefixcode.add(1);
			buildCList(internalNode.rightChild, prefixcode);
			prefixcode.remove(prefixcode.size() - 1);
			
		} else if (node1 instanceof LeafNode) {
			LeafNode leafnode = (LeafNode)node1;
			if (leafnode.sym >= hcode.size())
				throw new IllegalArgumentException("Symbol is out of limit");
			if (hcode.get(leafnode.sym) != null)
				throw new IllegalArgumentException("Symbol has more than one code");
			hcode.set(leafnode.sym, new ArrayList<Integer>(prefixcode));
			
		} else {
			throw new AssertionError("Invalid Symbol Node!");
		}
	}
	//Code for each symbol node in the code tree	
	public List<Integer> getCode(int sym) {
		if (sym < 0)
			throw new IllegalArgumentException("Invalid symbol!");
		else if (hcode.get(sym) == null)
			throw new IllegalArgumentException("No code for given symbol");
		else
			return hcode.get(sym);
	}

	// Symbols with respective codes
	public String toString() {
		StringBuilder s1 = new StringBuilder();
		toString("", root, s1);
		return s1.toString();
	}
		
	private static void toString(String prefixcode,Node node1, StringBuilder s1) {
		
		if (node1 instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node1;
			toString(prefixcode + "0", internalNode.leftChild , s1);
			toString(prefixcode + "1", internalNode.rightChild, s1);
		} else if (node1 instanceof LeafNode) {
			s1.append(String.format("Code %s: Symbol %d%n", prefixcode, ((LeafNode)node1).sym));
		} else {
			throw new AssertionError("Illegal node type");
		}
	}
}
/*
 * A FreqTable Class
 * - Collect the frequencies of symbols in the stream .
 * - Build a code tree for the symbol frequencies.
*/
final class FreqTable {
	
	private int[] frequencies;
	//	FreqTable Constructor
	public FreqTable(int[] freqs) {
		if (freqs == null)
			throw new NullPointerException("\n Frequencies are not provided");
		if (freqs.length < 2)
			throw new IllegalArgumentException("\n Minimun 2 frequencies required!");
			frequencies = freqs.clone();
		for (int x : frequencies) {
			if (x < 0)
				throw new IllegalArgumentException("Only positive frequenices allowed");
		}
	}
	//Get the length of the input bits
	public int getSymbolLimit() {
		return frequencies.length;
	}
	//Check the range of the symbols
	public int get(int sym) {
		if (sym < 0 || sym >= frequencies.length)
			throw new IllegalArgumentException("Symbol is invalid!");
		return frequencies[sym];
	}
	//Set the frequencies of each symbol taken from the stream
	public void set(int sym, int freq) {
		if (sym < 0 || sym >= frequencies.length)
			throw new IllegalArgumentException("Symbol is invalid!");
		frequencies[sym] = freq;
	}
	//check the frequencies of the overflow of the frequencies
	public void increment(int sym) {
		if (sym < 0 || sym >= frequencies.length)
			throw new IllegalArgumentException("Symbol out of range");
		if (frequencies[sym] == Integer.MAX_VALUE)
			throw new RuntimeException("Arithmetic overflow");
		frequencies[sym]++;
	}
	// Returns a string showing all the symbols and frequencies.
	public String toString() {
		StringBuilder s1 = new StringBuilder();
		for (int i = 0; i < frequencies.length; i++)
			s1.append(String.format("%d\t%d%n", i, frequencies[i]));
		return s1.toString();
	}
	
	// Generates a code tree for each symbol as leaf node. At least 2 symbols required!
	public CTree buildCTree() {
		
		// Create a priority queue to arrange the symbols in ascending order
		Queue<NodeFreq> mypriqueue = new PriorityQueue<NodeFreq>();
		
		
		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] > 0)
				mypriqueue.add(new NodeFreq(new LeafNode(i), i, frequencies[i]));
		}
		
		// Insert a '0' element in the queue to have at least two symbols in the queue
		for (int i = 0; i < frequencies.length && mypriqueue.size() < 2; i++) {
			if (i >= frequencies.length || frequencies[i] == 0)
				mypriqueue.add(new NodeFreq(new LeafNode(i), i, 0));
		}
		
		if (mypriqueue.size() < 2)
			throw new AssertionError();
		
		// create the tree nodes by selecting the two symbols with minimum frequencies
		while (mypriqueue.size() > 1) {
			NodeFreq node1 = mypriqueue.remove();
			NodeFreq node2 = mypriqueue.remove();
			mypriqueue.add(new NodeFreq(
					new InternalNode(node1.node, node2.node),
					Math.min(node1.LowerSym, node2.LowerSym),
					node1.Freq + node2.Freq));
		}
		
		// Return the remaining node
		return new CTree((InternalNode)mypriqueue.remove().node, frequencies.length);
	}
	
private static class NodeFreq implements Comparable<NodeFreq> {
		
		public final Node node;
		public final int LowerSym;
		public final long Freq;
		
		//NodeFreq constructor
		public NodeFreq(Node node, int lowerSym, long freq) {
			this.node = node;
			this.LowerSym = lowerSym;
			this.Freq = freq;
		}
		//Comparison of nodes as per the frequencies
		public int compareTo(NodeFreq Node1) {
			if (Freq < Node1.Freq)
				return -1;
			else if (Freq > Node1.Freq)
				return 1;
			else if (LowerSym < Node1.LowerSym)
				return -1;
			else if (LowerSym > Node1.LowerSym)
				return 1;
			else
				return 0;
		}
	}
	
}

final class BitOutStream {
	
	// Byte stream to write output in bytes.
	private OutputStream output;
	// The accumulated bits for the current byte. Always in the range 0x00 to 0xFF.
	private int CurrByte;
	// The number of accumulated bits in the current byte range from 0 to 7 bits.
	private int numBitsInCurrByte;
	
	// Bit output stream for byte output stream (constructor)
	public BitOutStream(OutputStream OutS) {
		if (OutS == null)
			throw new NullPointerException("Argument is null");
		output = OutS;
		CurrByte = 0;
		numBitsInCurrByte = 0;
	}
	// Writes a bit to the stream. The specified bit must be 0 or 1.
	public void write(int b) throws IOException {
		if (!(b == 0 || b == 1))
			throw new IllegalArgumentException("Input argument must be 0 / 1");
		CurrByte = CurrByte << 1 | b;
		numBitsInCurrByte++;
		if (numBitsInCurrByte == 8) {
			output.write(CurrByte);
			numBitsInCurrByte = 0;
		}
	}
	// Closes this BitOutStream OutputStream.
	// Padding is done in case of trailing bits of byte
	public void close() throws IOException {
		while (numBitsInCurrByte != 0)
			write(0);
		output.close();
	}
}
final class HuffmanEncode {
	
	private BitOutStream output;
	public CTree codeTree;
		
	//HuffmanEncode Constructor
	public HuffmanEncode(BitOutStream OutS) {
		if (OutS == null)
			throw new NullPointerException("Input is invalid!");
		output = OutS;
	}
	// Function to write to output stream	
	public void write(int sym) throws IOException {
		if (codeTree == null)
			throw new NullPointerException("Code tree is invalid");
		List<Integer> bits = codeTree.getCode(sym);
		for (int b : bits)
			output.write(b);
	}
}

public final class henc {

	public static void main(String[] args)throws IOException{
		
		// Input file passed as a argument
		/*if (args.length == 0) {
		 * System.err.println("\nPlease provide the Input File");
			System.err.println("\n Usage: java henc <InputFile>");
			System.exit(1);
			return;
		}*/
		
		//input file
		//File inputFile= new File(args[0]);
		//File inputFile= new File("C:\\Sandeep\\MSCS\\Projects\\Java_huffman_Final\\TEST\\test.txt");
		//File inputFile= new File("C:\\Sandeep\\MSCS\\Projects\\Java_huffman_Final\\TEST\\test1.jpg");
		//File inputFile= new File("C:\\Sandeep\\MSCS\\Projects\\Java_huffman_Final\\TEST\\test2.m4a");
		File inputFile= new File("/afs/cad.njit.edu/u/k/c/kcp35/test3.pdf");
		//Output file
		//File outputFile=new File(args[0]+".huf");
		//File outputFile=new File("C:\\Sandeep\\MSCS\\Projects\\Java_huffman_Final\\TEST\\test.txt"+".huf");
		//File outputFile=new File("C:\\Sandeep\\MSCS\\Projects\\Java_huffman_Final\\TEST\\test1.jpg"+".huf");
		//File outputFile=new File("C:\\Sandeep\\MSCS\\Projects\\Java_huffman_Final\\TEST\\test2.m4a"+".huf");
		File outputFile= new File("/afs/cad.njit.edu/u/k/c/kcp35/test3.pdf"+".huf");
		InputStream inFile= new BufferedInputStream(new FileInputStream(inputFile));
		BitOutStream outFile = new BitOutStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		
		try {
		CompressFile(inFile, outFile);
		
		} finally {
			outFile.close();
			inFile.close();
		}
	}

	static void CompressFile(InputStream inFile, BitOutStream outFile) throws IOException {
		int[] CharFreqs = new int[257];
		Arrays.fill(CharFreqs, 1);
		
		FreqTable freqTable = new FreqTable(CharFreqs);
		
		HuffmanEncode Encode1 = new HuffmanEncode(outFile);
		
		Encode1.codeTree = freqTable.buildCTree();
		int count = 0;
		while (true) {
			int input = inFile.read();
			if (input == -1)
				break;
			Encode1.write(input);
			
			freqTable.increment(input);
			count++;
			if (count < 262144 && IsPowerOf2(count) || count % 262144 == 0)
				Encode1.codeTree = freqTable.buildCTree();
			if (count % 262144 == 0)
				freqTable = new FreqTable(CharFreqs);
		}
		//Write the end of the file character
		Encode1.write(256);
	}
	//Function to implement power of 2 	
	private static boolean IsPowerOf2(int n) {
		return n > 0 && (n & -n) == n;
	}

}
