import java.io.File;
/**
 * @GeneBankCreateBTree:creates a BTree from a given GeneBank file.
 * 
 * @authors Michael Kinsy, Oscar Filson,    ,   ,
 *
 */
public class GeneBankCreateBTree {
	public static void main(String args[]) {
		//Instance Variables
		String has_Cache = "", input_degree = "", gbkFileName = "", input_seq_length = "", input_cache_size = "", debug_level = "";
		int degree, seq_length,cache_size,degree_level;
		//Args Parsing
		has_Cache = args[0]; input_degree = args[1];
		
		gbkFileName = args[2]; input_seq_length = args[3]; 
		
		input_cache_size = args[4]; debug_level = args[5];
		
		if(has_Cache == "1") {
			cache_size= Integer.valueOf(input_cache_size);
		}
		degree = Integer.valueOf(input_degree);
		File file = new File(gbkFileName);
		seq_length = Integer.valueOf(input_seq_length);
	}
	
	private class BTree{
		/**@InstanceVariables: for BTree*/
		private int degree;
		/**@Constructor: for BTree*/
		private BTree(int degree) {
			this.degree = degree;
		}
	}
	private class BTreeNode{
		/**@InstanceVariables: for BTreeNodes*/
		private String[] keys;
		private int min_degree,  num_keys;
		private BTreeNode[] Children;
		private boolean leaf;
		
		/**@Constructor: for BTreeNode*/
		private BTreeNode(int min_degree, boolean leaf) {
			this.min_degree = min_degree;
			this.leaf = leaf;
			this.keys = new String[2* min_degree-1];
			this.Children = new BTreeNode[2* min_degree];
			this.num_keys = 0;
		}
	}
}


