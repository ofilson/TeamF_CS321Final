import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

/**
 * Implementation of BTree for CS321 Final
 * @author Michael Kinsy, Oscar Filson,... ,... ,
 *
 */
public class BTree{
    /**@InstanceVariables: for BTree*/
	private int degree;
	private BTreeNode root;
	private int BTreeOffset;
	private int nodeSize;
	private int insertionPoint;
	private BTreeCacheNode.BTreeCache cache;
	private BTreeCacheNode BTCN;
	private File File;
	private RandomAccessFile disk;
    
    /**@Constructor: for Creating new BTree given a filename and degree
     * @param degree of BTree
     * @param fileName of BTree
     * 
     */
	public BTree (int deg, String FileName, boolean usingCache, int cacheSize) {
		nodeSize = (32 * deg - 3);
		BTreeOffset = 12;
		insertionPoint = (BTreeOffset + nodeSize);
		degree = deg;
		if (usingCache) {
			BTCN = new BTreeCacheNode(root, 0 ,cacheSize);
			cache = BTCN.BTC;
		}
		 BTreeNode x = new BTreeNode();
	        root = x;
	        root.setOffset(BTreeOffset);
	        x.setIsLeaf(true);
	        x.setNumKeys(0);
	        try {
	            File = new File(FileName);
	            File.delete();
	            File.createNewFile();    
	            disk = new RandomAccessFile(File, "rw");
	        }
	        catch (FileNotFoundException fnfe){
	            System.err.println("file is corrupt or missing!");
	            System.exit(-1);
	        }
	        catch (IOException ioe){
	            System.err.println("IO Exception occurred!");
	            System.exit(-1);
	        }
	    writeTreeMetadata();
	}
	
	 public BTree(int degree, File fileName, boolean usingCache, int cacheSize){
	        
	        try {
	            disk = new RandomAccessFile(fileName, "r");
	        }
	        catch (FileNotFoundException fnfe){
	            System.err.println("file is corrupt or missing!");
	            System.exit(-1);
	        }
		    readTreeMetadata();
	        root = readNode(BTreeOffset);
	    }
	
	 public BTree() {super();}
	
	public BTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Inserts into the BTree
	 */
	public void BTreeInsert(long key) {
		BTreeNode r = root;
		int i = r.getNumKeys();
		if (i == 2 * degree - 1) {
			BTreeObject Tobj = new BTreeObject(key);
			while (i > 0 && Tobj.compareTo(r.getKey(i-1)) < 0) {
				i--;
	        } if (i < r.getNumKeys()){ }
	        if (i > 0 && Tobj.compareTo(r.getKey(i-1)) == 0)
	        	r.getKey(i-1).increaseFreq();
	        else {
	        	BTreeNode s = new BTreeNode();
	        	s.setOffset(r.getOffset());
	       		root = s;
	       		r.setOffset(insertionPoint);
	       		r.setParent(s.getOffset());
	       		s.setIsLeaf(false);
	       		s.addChild(r.getOffset());
	       		splitChild(s, 0, r);
	       		BTreeInsertNonFull(s,key);
	       	}
        } else
           BTreeInsertNonFull(r,key);
	}
	
	public void BTreeInsertNonFull(BTreeNode x, long key) {
		int i = x.getNumKeys();
		
		BTreeObject Tobj = new BTreeObject(key);
		if (x.isLeaf()) {
			if (x.getNumKeys() != 0) {
                while (i > 0 && Tobj.compareTo(x.getKey(i-1)) < 0) {
                    i--;
                }
            } if (i > 0 && Tobj.compareTo(x.getKey(i-1)) == 0) {
                x.getKey(i-1).increaseFreq();
            } else {
                x.addKey(Tobj,i);
                x.setNumKeys(x.getNumKeys()+1);
            }
            writeNode(x,x.getOffset());
        } else {
            while (i > 0 && (Tobj.compareTo(x.getKey(i-1)) < 0)) {
                i--;
            } if (i > 0 && Tobj.compareTo(x.getKey(i-1)) == 0) {
                x.getKey(i-1).increaseFreq();
                writeNode(x,x.getOffset());
                return;
            }
            int offset = x.getChild(i);
            BTreeNode y = readNode(offset);
            if (y.getNumKeys() == 2 * degree - 1) {
                int j = y.getNumKeys();
                while (j > 0 && Tobj.compareTo(y.getKey(j-1)) < 0) {
                    j--;
                } if (j > 0 && Tobj.compareTo(y.getKey(j-1)) == 0) {
                    y.getKey(j-1).increaseFreq();
                    writeNode(y,y.getOffset());
                    return;
                } else {
                    splitChild(x, i, y);
                        if (Tobj.compareTo(x.getKey(i)) > 0) {
                            i++;
                        }
                }
            }
            offset = x.getChild(i);
            BTreeNode child = readNode(offset);
            BTreeInsertNonFull(child,key);
        }
	}
	
	public void splitChild(BTreeNode x, int i, BTreeNode y) {
        BTreeNode z = new BTreeNode();
        z.setIsLeaf(y.isLeaf());
        z.setParent(y.getParent());
        for (int j = 0; j < degree - 1; j++) {
            z.addKey(y.removeKey(degree));
            z.setNumKeys(z.getNumKeys()+1);
            y.setNumKeys(y.getNumKeys()-1);

        } if (!y.isLeaf()) {
            for (int j = 0; j < degree; j++) {
                z.addChild(y.removeChild(degree));
            }
        }
        x.addKey(y.removeKey(degree - 1), i);
        x.setNumKeys(x.getNumKeys()+1);
        y.setNumKeys(y.getNumKeys()-1);
        if (x == root && x.getNumKeys() == 1) {
            writeNode(y,insertionPoint);
            insertionPoint += nodeSize;
            z.setOffset(insertionPoint);
            x.addChild(z.getOffset(),i+1);
           writeNode(z,insertionPoint);
           writeNode(x,BTreeOffset);
            insertionPoint += nodeSize;
        } else {
            writeNode(y,y.getOffset());
            z.setOffset(insertionPoint);
            writeNode(z,insertionPoint);
            x.addChild(z.getOffset(),i+1);
            writeNode(x,x.getOffset());
            insertionPoint += nodeSize;
        }
    }
	
	public BTreeObject search(BTreeNode x, long key) {
        int i = 0;
        BTreeObject obj = new BTreeObject(key);
        while (i < x.getNumKeys() && (obj.compareTo(x.getKey(i)) > 0)) {
            i++;
        } if (i < x.getNumKeys() && obj.compareTo(x.getKey(i)) == 0) {
            return x.getKey(i);
        } if (x.isLeaf()) {
            return null;
        } else {
            int offset = x.getChild(i);
            BTreeNode y = readNode(offset);
            return search(y,key);
        }
    }
	
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
	
	public void inOrderPrintToWriter(BTreeNode node, PrintWriter PWriter, int sequenceLength) throws IOException {
        GeneBankConvert gbc = new GeneBankConvert();
        for (int i = 0; i < node.getNumKeys(); i++){
            PWriter.print(gbc.convertLongToString(node.getKey(i).getData(),sequenceLength)+ " ");
            PWriter.println(node.getKey(i).getFreq());
        } if (!node.isLeaf()) {
	        for (int i = 0; i < node.getNumKeys() + 1; ++i) {
	            int offset = node.getChild(i);
	            BTreeNode y = readNode(offset);
	            inOrderPrintToWriter(y,PWriter,sequenceLength);
	            if (i < node.getNumKeys()) {
                    PWriter.print(gbc.convertLongToString(node.getKey(i).getData(),sequenceLength)+ " ");
	                PWriter.println(node.getKey(i).getFreq());
	            }
	        }
        }
    }
	
	public void writeNode(BTreeNode n, int offset){
        if (cache != null) {
        	BTreeNode cnode = cache.add(n, offset);
        	// if a node was pushed off, write it
        	if (cnode != null) writeNodeToFile(cnode,cnode.getOffset());
        } else {
        	writeNodeToFile(n, offset);
        }
    }
	
	   private void writeNodeToFile(BTreeNode n, int offset) {
	        int i = 0;
	        try {
	            writeNodeMetadata(n,n.getOffset());
	            disk.writeInt(n.getParent());
	            for (i = 0; i < 2 * degree - 1; i++){
	                if (i < n.getNumKeys() + 1 && !n.isLeaf()) {
	                    disk.writeInt(n.getChild(i));
	                } else if (i >= n.getNumKeys() + 1 || n.isLeaf()) {
	                    disk.writeInt(0);
	                } if (i < n.getNumKeys()){
	                    long data = n.getKey(i).getData();
	                    disk.writeLong(data);
	                    int frequency = n.getKey(i).getFreq();
	                    disk.writeInt(frequency);
	                } else if (i >= n.getNumKeys() || n.isLeaf()) {
	                    disk.writeLong(0);
	                }
	            } if (i == n.getNumKeys() && !n.isLeaf()) {
	                disk.writeInt(n.getChild(i));
	            }
	        } catch (IOException ioe) {
	            System.err.println("IO Exception occurred!");
	            System.exit(-1);
	        }
	    }
	
	    public BTreeNode readNode(int offset){
	    	
	    	BTreeNode y = null;
	    	
	    	// if node is cached, we can just read it from there
	        if (cache != null) y = cache.readNode(offset);
	        if (y != null) return y;
	        
	        y = new BTreeNode();
	        BTreeObject obj = null;
	        y.setOffset(offset);
	        int k = 0;
	        try {
	            disk.seek(offset);
	            boolean isLeaf = disk.readBoolean();
	            y.setIsLeaf(isLeaf);
	            int n = disk.readInt();
	            y.setNumKeys(n);
	            int parent = disk.readInt();
	            y.setParent(parent);
	            for (k = 0; k < 2 * degree - 1; k++) {
	                if (k < y.getNumKeys() + 1 && !y.isLeaf()) {
	                    int child = disk.readInt();
	                    y.addChild(child);
	                } else if (k >= y.getNumKeys() + 1 || y.isLeaf()) {
	                    disk.seek(disk.getFilePointer() + 4);
	                } if (k < y.getNumKeys()){
	                    long value = disk.readLong();
	                    int frequency = disk.readInt();
	                    obj = new BTreeObject(value,frequency);
	                    y.addKey(obj);
	                }
	            } if (k == y.getNumKeys() && !y.isLeaf()) {
	                int child = disk.readInt();
	                y.addChild(child);
	            }
	        } catch (IOException ioe) {
	            System.err.println(ioe.getMessage());
	            System.exit(-1);
	        }
	        
	        return y;
	    }
	   
	    public void writeTreeMetadata() {
	        try {
	            disk.seek(0);
	            disk.writeInt(degree);
	            disk.writeInt(32 * degree - 3);
	            disk.writeInt(12);
	        } catch (IOException ioe) {
	            System.err.println("IO Exception occurred!");
	            System.exit(-1);
	        }
	    }
	    public void readTreeMetadata() {
	        try {
	            disk.seek(0);
	            degree = disk.readInt();
	            nodeSize = disk.readInt();
	            BTreeOffset = disk.readInt();
	        } catch (IOException ioe) {
	            System.err.println("IO Exception occurred!");
	            System.exit(-1);
	        }
	    }
	    public void writeNodeMetadata(BTreeNode x, int offset) {
	        try {
	            disk.seek(offset);
	            disk.writeBoolean(x.isLeaf());
	            disk.writeInt(x.getNumKeys());
	        } catch (IOException ioe){
	            System.err.println("IO Exception occurred!");
	            System.exit(-1);
	        }
	    }
	    
	    public void flushCache() {
	    	if (cache != null) {
	    		for (BTreeNode cnode : cache) writeNodeToFile(cnode,cnode.getOffset());
	    	}
	    }

	public String Convert(long key) {
		String result = "";
		if (key == -1) {
			return result;
		}
		String temp = "";
		String temp2 = "";
		temp = Long.toBinaryString(key);
		for (int i = insertionPoint * 2; i > 1; i -= 2) {
			try{
				temp2 = temp.substring(i - 1, i + 1);
				if (temp2.equals("00")) result = result + "A";
				else if (temp2.equals("11")) result = result + "T";
				else if (temp2.equals("01")) result = result + "C";
				else if (temp2.equals("10")) result = result + "G";	
			}
			catch(StringIndexOutOfBoundsException ex) {
				
			}
		}
		return result;
	}
	
}