import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
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
			cache_size = Integer.valueOf(input_cache_size);
		}

		degree = Integer.valueOf(input_degree);
		File file = new File(gbkFileName);
		seq_length = Integer.valueOf(input_seq_length);

		if(!(seq_length <= 31) && !(seq_length >= 1)){ //Makes sure that the seq_length is between 1 and 31
			throw new IndexOutOfBoundsException();
		}

		ArrayList<String> seqList = new ArrayList<String>(); //Holds all sequences (sometimes there are more than one in a gbk file.)

		
		try { //Navigates through given .gbk file and places full sequences in seqList as strings.
			Scanner fileScan = new Scanner(file);
			while(fileScan.hasNextLine()){
				String line = fileScan.nextLine();
				if(line.contains("ORIGIN")){ //Finds sequence beginning
					StringBuilder seq = new StringBuilder();
					line = fileScan.nextLine();
					while(!line.contains("//")){ //Stops at //
						Scanner lineScan = new Scanner(line);

						while(lineScan.hasNext()){
							if(lineScan.hasNextInt()){	// This removes line numbers at beginning of the line
								lineScan.next();		
							}							
							String val = lineScan.next(); //Grabs next 8 characters
							if(!val.contains("N") && !val.contains("n")){ //If 8 char sequence doens't contain n
								seq.append(val); //Add to sequence
							} else { //If it does contain an n
								while(val.contains("n")){ //gets rid of all lowercase n's
									String part1 = val.substring(0, val.indexOf("n"));
									String part2 = val.substring(val.indexOf("n") + 1, val.length());
									val = part1 + part2;
								}
								while(val.contains("N")){ //gets rid of all uppercast N's
									String part1 = val.substring(0, val.indexOf("N"));
									String part2 = val.substring(val.indexOf("N") + 1, val.length());
									val = part1 + part2;
								}
								seq.append(val); //Adds remainder of 8 char sequence that isn't an n to sequence.
							}
						}
						line = fileScan.nextLine();
					}
					seqList.add(seq.toString()); //Sequence added to list of sequences.
					//System.out.println(seq.toString()); //Print for debugging pruposes
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
		}

		ArrayList<BTree> treeList = new ArrayList<BTree>();					//Basically seqList except for their BTree version

		//Creating the BTree nodes
		for(int i = 0; i < seqList.size(); i++) { //For every full sequence in our seqlist (shouldn't be many)
			String fullSeq = seqList.get(i);
			int start = 0; 
			int finish = seq_length;
			BTree tree = new BTree(degree); //Make a new BTree

			while(finish < fullSeq.length()) { //This is a big one, increments start and finish by one until we've got subsequences of length m for the whole array
				long output;
				String outputString = "+";
				String initialString = fullSeq.substring(start, finish);
				for(int y = 0; y < initialString.length(); y++){ //Converts the subsequence into a binary long, runs less than 31 times
					char base = initialString.charAt(y);
					if(base == 'a') {
						outputString += "00";
					} else if (base == 't') {
						outputString += "11";
					} else if (base == 'c') {
						outputString += "01";
					} else if (base == 'g') {
						outputString += "10";
					} else {
						throw new IndexOutOfBoundsException();
					}
				}
				output = Long.parseLong(outputString,2); 		//Turns String into binary long
				BTreeNode node = new BTreeNode(degree, output); //Makes a BTreeNode with that long as the key
				tree.add(node); 								//adds it to the BTree
				start++;										//Increment start and finish
				finish++;
			}
			treeList.add(tree);									//Add the tree to the list of trees (one tree per full sequence)
		}

	}

}


