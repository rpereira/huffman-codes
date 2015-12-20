/*******************************************************************************
 * Compilation:  javac Huffman.java
 * Execution:    java Huffman --compress input.txt
 * Dependencies: BinaryIn.java
 *               BinaryOut.java*
 *
 ******************************************************************************/

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.PriorityQueue;

/**
 * The Huffman css provides methods to compress and expand data using Huffman
 * codes over the 8-bit extended ACII alphabet.
 */
public class Huffman
{
  // Size of extended ASCII alphabet.
  private static final int ALPHABET_SIZE = 256;

  private Huffman() { }

  /**
   * Huffman trie node.
   * Each node has left and right references to Nodes, which define the trie
   * structure. Each Node also has an instance variable freq that is used in
   * contruction, and and instance variable ch, which is used in leaves to
   * represent characters to be encoded.
   */
  private static class Node implements Comparable<Node>
  {
    private final Node left;
    private final Node right;
    private int freq;
    private char ch;

    Node(Node left, Node right, char ch, int freq)
    {
      this.left  = left;
      this.right = right;
      this.freq  = freq;
      this.ch    = ch;
    }

    /**
     * Compares based on the frequency.
     */
    public int compareTo(Node that)
    {
      return this.freq - that.freq;
    }

    /**
     * Returns true if this node is a leaf node.
     */
    public boolean isLeaf()
    {
      assert ((left == null) && (right == null)) ||
             ((left != null) && (right != null));
      return left == null && right == null;
    }
  }

  /**
   * Reads a sequence of 8-bit bytes from standard input; compresses them using
   * Huffman codes with an 8-bit alphabet; and writes the results to standard
   * output.
   */
  public static void compress()
  {
    // read input
    String s     = BinaryStdIn.readString();
    char[] input = s.toCharArray();

    // tabulate frequency counts
    int[] charFreq = new int[ALPHABET_SIZE];
    for (int i = 0; i < input.length; i++)
      charFreq[input[i]]++;

    // build Huffman trie
    Node root = buildTrie(charFreq);

    // print code table
    String[] st = new String[ALPHABET_SIZE];
    buildCode(st, root, "");

    // print trie for decoder
    writeTrie(root);

    // print the number of bytes in original uncompressed message
    BinaryStdOut.write(input.length);

    // use Huffman code to encode input
    for (int i = 0; i < input.length; i++)
    {
      String code = st[input[i]];
      for (int j = 0; j < code.length(); j++)
      {
        if (code.charAt(j) == '0')
          BinaryStdOut.write(false);
        else if (code.charAt(j) == '1')
          BinaryStdOut.write(true);
        else
          throw new IllegalStateException("Illegal state");
      }
    }

    // close output stream
    BinaryStdOut.close();
  }

  /**
   * Build the Huffman trie based on the given char frequencies.
   */
  private static Node buildTrie(int[] charFreq)
  {
    PriorityQueue<Node> minPq = new PriorityQueue<Node>();
    for (char i = 0; i < ALPHABET_SIZE; i++)
      if (charFreq[i] > 0)
        minPq.offer(new Node(null, null, i, charFreq[i]));

    assert minPq.size() > 0;

    while (minPq.size() > 1)
    {
      Node left   = minPq.poll();
      Node right  = minPq.poll();
      Node parent = new Node(left, right, '\0', left.freq + right.freq);
      minPq.offer(parent);
    }

    return minPq.poll();
  }

  /**
   * Write bitstring-encoded trie to standard output.
   */
  private static void writeTrie(Node x) {
    if (x.isLeaf())
    {
      BinaryStdOut.write(true);
      BinaryStdOut.write(x.ch, 8);
      return;
    }

    BinaryStdOut.write(false);
    writeTrie(x.left);
    writeTrie(x.right);
  }

  /**
   * Make a lookup table from symbols and their encodings.
   */
  private static void buildCode(String[] st, Node x, String s) {
    if (!x.isLeaf()) {
      buildCode(st, x.left,  s + '0');
      buildCode(st, x.right, s + '1');
    }
    else {
      st[x.ch] = s;
    }
  }

  public static void expand()
  {
    // TODO
  }

  public static void main(String[] args)
  {
    if(args[0].equals("--compress"))
      compress();
    else if(args[0].equals("expand"))
      expand();
    else
      throw new IllegalArgumentException("huffman: no such command");
  }
}
