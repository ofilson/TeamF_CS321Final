import java.io.File;
/**
 * @GeneBankSearch:Searches given Btree for DNA sequences of given length.
 * 
 * @authors Michael Kinsy, Oscar Filson,    ,   ,
 *
 */
public class GeneBankSearch {
	public static void main(String args[]) {
		//Instance Variables
		String has_Cache = "", BTreeFileName = "", queryFileName = "", input_cache_size = "", debug_level = "";
		int cache_size;
		//Args Parsing
		has_Cache = args[0]; BTreeFileName = args[1]; 
		
		queryFileName = args[2];  input_cache_size = args[3]; 
		
		debug_level = args[4];
		
		if(has_Cache == "1") {
			cache_size= Integer.valueOf(input_cache_size);
		}
	
		File BTreeFile = new File(BTreeFileName), queryFile = new File(queryFileName);
		
	}
}
