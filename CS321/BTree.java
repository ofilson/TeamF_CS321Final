import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

/**
 * Implementation of BTree for CS321 Final
 * @author Michael Kinsy,... ,... ,... ,
 *
 */
public class BTree{
    /**@InstanceVariables: for BTree*/
    private int degree;//Degree for BTree
    private BTreeNode rootNode; //Root node to keep direct reference to root of tree
    private int insertPoint; //current Byte postion for file on disk 
    private int rootNodeOffset; //Byte postion of root node for disk file
    private int nodeSize; //Size of a node object in tree
    private Cache BTreeCache;//Cache to store BTreeNodes
    private int cacheSize;//size of BTreeCache
    private RandomAccessFile disk; //RAF object to write and read data from file on disk
    private String fileName; //File name of BTree
    
    /**@Constructor: for Creating new BTree given a filename and degree
     * @param degree of BTree
     * @param fileName of BTree
     * 
     */
    public BTree(int degree,String fileName) {
        this.degree = degree;
        this.fileName = fileName;
        nodeSize = 32*degree-3; //
        rootNodeOffset = 12;
        insertPoint = rootNodeOffset + nodeSize;
        BTreeNode temp = new BTreeNode(true);
        rootNode = temp;
        rootNode.setByteOffset(rootNodeOffset);
        
        try {
        	File file = new File(fileName);
        	file.delete(); //delete the file if it already exists in the directory
        	file.createNewFile();
        	disk = new RandomAccessFile(file, "rw");
        	disk.seek(0);
        	disk.writeInt(degree);
        	disk.writeInt(nodeSize);
        	disk.writeInt(rootNodeOffset);
        	insertPoint = 12;
        }catch(IOException e) {
        	System.err.println("Error: Occured in I/O in BTree construction");
        }
    }
    
    
    /**@Constructor: for Creating new BTree given from a existing BTree-filename
     * @param fileName
     */
    public BTree(String fileName) {
        this.fileName = fileName;
        try {
        	File file = new File(fileName);
        	disk = new RandomAccessFile(file, "rw");
        	this.degree = disk.readInt();
        	nodeSize = disk.readInt();
        	rootNodeOffset = disk.readInt();
        	rootNode = readNode(rootNodeOffset);
        }catch(IOException e) {
        	System.err.println("Error: Occured in I/O in BTree construction");
        }
    }
    /**
	 * Inserts a new key into the BTree
	 * @param key 
	 */
	public void BTreeInsert(long key) {
		BTreeNode r = rootNode;
		int rootNumKeys = r.getNumKeys();
		if (rootNumKeys == 2 * degree - 1) { //If the root node is full
			BTreeObject obj = new BTreeObject(key); //make a new BTreeObject to store key
			//checks to see if any keys in root are less than obj's key 
			while (rootNumKeys > 0 && obj.compareTo(r.getKey(rootNumKeys-1)) < 0) {
				rootNumKeys--; //decrements the number of keys that are less than obj's key
	        } //if (rootNumKeys < r.getNumKeys()){ }
	        if (rootNumKeys > 0 && obj.compareTo(r.getKey(rootNumKeys-1)) == 0) {//checks root node has any keys the same as obj 
	        	r.getKey(rootNumKeys-1).increaseFreq();//if so increase frequency of key
	        }else {
	        	BTreeNode node = new BTreeNode(false);//create a new node
	        	node.setByteOffset(r.getByteOffset());//set its byte offset to roots
	       		rootNode= node;//set rootNode reference to node
	       		r.setByteOffset(insertPoint); //set the old roots new byte offset
	       		r.setParentPointer(node.getByteOffset());//set new node as old roots parent
	       		node.addChild(r.getByteOffset());//add old root as new nodes child
	       		splitChild(node, 0, r);//split node and r 
	       		insertNonFull(node,key);
	       	}
        } else
           insertNonFull(r,key);
	}
	/**
	 * Inserts a key into a non full BTree Node
	 * @param node that will store key
	 * @param key to be inserted into node
	 */
	public void insertNonFull(BTreeNode node, long key) {
		int nodeNumKeys = node.getNumKeys();
		
		BTreeObject obj = new BTreeObject(key);
		if (node.isLeaf()) {
			if (node.getNumKeys() != 0) {
                while (nodeNumKeys > 0 && obj.compareTo(node.getKey(nodeNumKeys-1)) < 0) {
                    nodeNumKeys--;
                }
            } if (nodeNumKeys > 0 && obj.compareTo(node.getKey(nodeNumKeys-1)) == 0) {
                node.getKey(nodeNumKeys-1).increaseFreq();
            } else {
                node.addKey(obj);
                node.setNumKeys(node.getNumKeys()+1);
            }
            writeNode(node,node.getByteOffset());
        } else { //Incase node is not a leaf
            while (nodeNumKeys > 0 && (obj.compareTo(node.getKey(nodeNumKeys-1)) < 0)) {
                nodeNumKeys--;
            } if (nodeNumKeys > 0 && obj.compareTo(node.getKey(nodeNumKeys-1)) == 0) {
                node.getKey(nodeNumKeys-1).increaseFreq();
                writeNode(node,node.getByteOffset());
                return;
            }
            int childNodeOffset = node.getChild(nodeNumKeys);
            BTreeNode childNode = readNode(childNodeOffset);
            if (childNode.getNumKeys() == 2 * degree - 1) {
                int node2NumKeys = childNode.getNumKeys();
                while (node2NumKeys > 0 && obj.compareTo(childNode.getKey(node2NumKeys-1)) < 0) {
                    node2NumKeys--;
                } if (node2NumKeys > 0 && obj.compareTo(childNode.getKey(node2NumKeys-1)) == 0) {
                    childNode.getKey(node2NumKeys-1).increaseFreq();
                    writeNode(childNode,childNode.getByteOffset());
                    return;
                } else {
                    splitChild(node, nodeNumKeys, childNode);
                        if (obj.compareTo(node.getKey(nodeNumKeys)) > 0) {
                            nodeNumKeys++;
                        }
                }
            }
            childNodeOffset = node.getChild(nodeNumKeys);
            BTreeNode child = readNode(childNodeOffset);
            insertNonFull(child,key);
        }
	}
	/**
	 * When the child node of a parent is full and can hold no more object this method 
	 * will be called thus splitting the node in two
	 * 
	 * @param parentNode the ancestor of the node being split 
	 * @param i the number of keys for node1
	 * @param childNode the node being split
	 * @throws Exception
	 */
	public void splitChild(BTreeNode parentNode, int i, BTreeNode childNode) {
        BTreeNode node3 = new BTreeNode(childNode.isLeaf());
        node3.setParentPointer(childNode.getParentPointer());
        for (int j = 0; j < degree - 1; j++) {
            node3.addKey(childNode.removeKey(degree));
            node3.setNumKeys(node3.getNumKeys()+1);
            childNode.setNumKeys(childNode.getNumKeys()-1);

        } if (!childNode.isLeaf()) {
            for (int j = 0; j < degree; j++) {
                node3.addChild(childNode.removeChild(degree));
            }
        }
        parentNode.addKey(childNode.removeKey(degree - 1), i);
        parentNode.setNumKeys(parentNode.getNumKeys()+1);
        childNode.setNumKeys(childNode.getNumKeys()-1);
        if (parentNode == rootNode && parentNode.getNumKeys() == 1) {
            writeNode(childNode,insertPoint);
            insertPoint += nodeSize;
            node3.setByteOffset(insertPoint);
            parentNode.addChild(node3.getByteOffset(),i+1);
           writeNode(node3,insertPoint);
           writeNode(parentNode,rootNodeOffset);
            insertPoint += nodeSize;
        } else {
            writeNode(childNode,childNode.getByteOffset());
            node3.setByteOffset(insertPoint);
            writeNode(node3,insertPoint);
            parentNode.addChild(node3.getByteOffset(),i+1);
            writeNode(parentNode,parentNode.getByteOffset());
            insertPoint += nodeSize;
        }
    }
	
	/**
	 * Searches given BTreeNode for a key
	 * @param node
	 * @param key
	 * @return key if found in node
	 */
	public BTreeObject search(BTreeNode node, long key) {
        int i = 0;
        BTreeObject obj = new BTreeObject(key);
        while (i < node.getNumKeys() && (obj.compareTo(node.getKey(i)) > 0)) {//searches through node till key is not greater than node key
            i++;
        } if (i < node.getNumKeys() && obj.compareTo(node.getKey(i)) == 0) {//if node has key
            return node.getKey(i);
        } if (node.isLeaf()) {//if node doesnt have key and no children
            return null;
        } else {//searches children recursively 
            int offset = node.getChild(i);
            BTreeNode temp = readNode(offset);
            return search(temp,key);
        }
    }
	
	/**
	 * Prints out all of the keys from BTree in order
	 * @param node
	 */
	public void inOrderPrint(BTreeNode node) {
        System.out.println(node);
        if (node.isLeaf() == true){
            for (int i = 0; i < node.getNumKeys(); i++) {
                System.out.println(node.getKey(i));
            } return;
        }
        for (int i = 0; i < node.getNumKeys() + 1; ++i) {
            int offset = node.getChild(i);
            BTreeNode y = readNode(offset);
            inOrderPrint(y);
            if (i < node.getNumKeys())
                System.out.println(node.getKey(i));
        }
    }
	
	/**
	 * Prints all of the keys and frequencies to a printwriter
	 * @param node
	 * @param PWriter passed print writer to write to a file
	 * @param sequenceLength
	 * @throws IOException
	 */
	public void inOrderPrintToWriter(BTreeNode node, PrintWriter PWriter, int sequenceLength) throws IOException {
        for (int i = 0; i < node.getNumKeys(); i++){
            PWriter.print(node.getKey(i).getFreq()+ " ");
            PWriter.println(convertLongToString(node.getKey(i).getValue(),sequenceLength));
        } if (!node.isLeaf()) {
	        for (int i = 0; i < node.getNumKeys() + 1; ++i) {
	            int offset = node.getChild(i);
	            BTreeNode y = readNode(offset);
	            inOrderPrintToWriter(y,PWriter,sequenceLength);
	            if (i < node.getNumKeys()) {
	                PWriter.print(node.getKey(i).getFreq() + " ");
                    PWriter.println(convertLongToString(node.getKey(i).getValue(),sequenceLength));
	            }
	        }
        }
    }
	/**
	 * Writes node to disk at a given offset and checks if it is on the cache
	 * @param node	 node to be written to disk
	 * @param offset location to write on disk
	 */
	public void writeNode(BTreeNode node, int offset){
        if (BTreeCache != null) {
        	
			@SuppressWarnings("unchecked")
			BTreeNode cacheNode = (BTreeNode) BTreeCache.addObject(node);
        	// if the node hits on the cache write it then
        	if (cacheNode != null) writeNodeToFile(cacheNode,cacheNode.getByteOffset());
        } else {
        	writeNodeToFile(node, offset);
        }
    }
	/**
	 * Method that takes node and offset to write node to disk
	 * @param node
	 * @param offset
	 */
	   private void writeNodeToFile(BTreeNode node, int offset) {
	        int i = 0;
	        try {
	            writeNodeMetadata(node,node.getByteOffset());
	            disk.writeInt(node.getParentPointer());
	            for (i = 0; i < 2 * degree - 1; i++){
	                if (i < node.getNumKeys() + 1 && !node.isLeaf()) {
	                    disk.writeInt(node.getChild(i));
	                } else if (i >= node.getNumKeys() + 1 || node.isLeaf()) {
	                    disk.writeInt(0);
	                } if (i < node.getNumKeys()){
	                    long data = node.getKey(i).getValue();
	                    disk.writeLong(data);
	                    int frequency = node.getKey(i).getFreq();
	                    disk.writeInt(frequency);
	                } else if (i >= node.getNumKeys() || node.isLeaf()) {
	                    disk.writeLong(0);
	                }
	            } if (i == node.getNumKeys() && !node.isLeaf()) {
	                disk.writeInt(node.getChild(i));
	            }
	        } catch (IOException e) {
	            System.err.println("IO Exception occurred!");
	            System.exit(-1);
	        }
	    }
	   
	   /**
	    * Writes nodes meta data to disk
	    * @param node
	    * @param offset
	    */
	   public void writeNodeMetadata(BTreeNode node, int offset) {
	        try {
	            disk.seek(offset);
	            disk.writeBoolean(node.isLeaf());
	            disk.writeInt(node.getNumKeys());
	        } catch (IOException ioe){
	            System.err.println("IO Exception occurred!");
	            System.exit(-1);
	        }
	    }
    /**
     * Reads in node from disk at given offset
     * @param byte offset
     * @return BTreeNode at byte offset from disk
     */
    public BTreeNode readNode(int offset){
    	
    	BTreeNode temp = null;
    	
        if (BTreeCache != null) {
        	temp = BTreeCache.readNode(offset);
        	}
        if (temp != null) { 
        	return temp;
        	}
        
        temp = new BTreeNode(false);
        BTreeObject obj = null;
        temp.setByteOffset(offset);
        int k = 0;
        try {
        	disk.seek(offset);
            boolean isLeaf = disk.readBoolean();
            temp.setLeaf(isLeaf);
            int numKeys =	disk.readInt();
            temp.setNumKeys(numKeys);
            int parentPointer = disk.readInt();
            temp.setParentPointer(parentPointer);
            for (k= 0; k < 2 * degree - 1; k++) {
                if (k < temp.getNumKeys() + 1 && !temp.isLeaf()) {
                    int child = disk.readInt();
                    temp.addChild(child);
                } else if (k >= temp.getNumKeys() + 1 || temp.isLeaf()) {
                    disk.seek(disk.getFilePointer() + 4);
                } if (k < temp.getNumKeys()){
                    long value = disk.readLong();
                    int frequency = disk.readInt();
                    obj = new BTreeObject(value,frequency);
                    temp.addKey(obj);
                }
            } if (k == temp.getNumKeys() && !temp.isLeaf()) {
                int child = disk.readInt();
                temp.addChild(child);
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            System.exit(-1);
        }
        
        return temp;
    }
   /**
    * Converts given binary key into string
    * @param key
    * @param sequenceLength
    * @return key in String form
    * @note 0b<number> is just literal form of a binary digit
    */
   private static String convertLongToString(long key, int sequenceLength) {
		StringBuilder result = new StringBuilder();
		long temp;
		
		for (int i = 1; i <= sequenceLength; i++) {
			temp = (key & 0b11 << (sequenceLength-i)*2);
			temp = temp >> (sequenceLength-i)*2;
			
			if (temp == 0b0) {
				result.append("a");
			} else if (temp == 0b1) {
				result.append("c");
			} else if (temp == 0b10) {
				result.append("g");
			} else if (temp == 0b11) {
				result.append("t");
			}
		} return result.toString();
	}
	
    
}