import java.io.File;
import java.util.Scanner;
/**
 * @GeneBankSearch:Searches given Btree for DNA sequences of given length.
 * 
 * @authors Michael Kinsy, Oscar Filson,    ,   ,
 *
 */
public class GeneBankSearch {
	private static boolean useCache = false;
	private static String btreeFile, queryFile;
	private static int cacheSize = 0;

	public static void main(String[] args) {
		String seq = "", deg = "";
		//verify valid argument length
		if(args.length < 3 || args.length > 5) {
			printUsage();
		}

		if (args[0].equals("1")) {
			useCache = true; //use BTree with cache
		} else if (!(args[0].equals("0") || args[0].equals("1"))) {
			printUsage();
		} 

		btreeFile = args[1]; //BTree File
		queryFile = args[2]; //Query File

		if (useCache && args.length >= 4) {
			cacheSize = Integer.parseInt(args[3]);
		}

		if(args.length == 5){
		}

		//Find degree
		for(int i = btreeFile.length()-1; i >= 0; i--) {
			if(btreeFile.charAt(i) != '.')
				deg += btreeFile.charAt(i);
			else break;
		}
		deg = reverseString(deg);

		//Find sequence length
		for (int i = btreeFile.length()-deg.length()-2; i >= 0; i--) {
			if(btreeFile.charAt(i) != '.')
				seq += btreeFile.charAt(i);
			else break;
		}
		seq = reverseString(seq);

		int degree = Integer.parseInt(deg);
		
		//
		try {
			GeneBankConvert gbc = new GeneBankConvert();
			BTree tree = new BTree(degree, new File(btreeFile), useCache, cacheSize);
			Scanner scan = new Scanner(new File(queryFile));
			
			while(scan.hasNext()) {
				String query = scan.nextLine(); //what to search for
				
				long q = gbc.convertStringToLong(query);
				BTreeObject result = tree.search(tree.getRoot(), q);
				
				if(result != null) {
					System.out.println(gbc.convertLongToString(result.getData(), Integer.parseInt(seq))+": "+ result.getFreq());
				}
			}
			
			scan.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recursively calls itself to return a reversed version of the argument string
	 * @param s - String to reverse
	 * @return - Reversed String of parameter s
	 */
	private static String reverseString(String s) {
		if(s.length() == 1)
			return s;
		return "" + s.charAt(s.length() - 1) + reverseString(s.substring(0, s.length() - 1));
	}

	/**
	 * Called when the arguments do not fit what is expected. 
	 * Prints out the correct argument usage to the console. 
	 */
	private static void printUsage() {
		System.err.println("Usage: java GeneBankSearch <cache)> <btree file> <query file> [<cache size>] [<debug level>]\n");
		System.err.println("<cache>: 0/1 (no cache/cache)");
		System.err.println("<btree file>: file containing the BTree.");
		System.err.println("<query file>: file containing the sequences to search the BTree for");
		System.err.println("[<cache size>]: size of cache if one is being used, optional");
		System.err.println("[<debug level>]: 0 if debug is wanted.");
		System.exit(1); 
	}
}
