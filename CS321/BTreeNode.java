import java.util.ArrayList;
/**
 * This class represents a BTreeNode to be stored in BTree
 * -----FUCNTIONALITY-----
 * stores BTreeObjects as keys in an array list
 * has reference to a parent node by a pointer from the parents Integer byteOffset from the RandomAccessFile
 * also has reference to an arraylist of children node pointers same as parent pointer
 * ------------------------
 * @author MichaelKinsy, , , ,
 *
 */
public class BTreeNode{
    /**@InstanceVariables: for BTreeNodes*/
    private ArrayList<BTreeObject> keys; //All of the key values for the node
    private int numKeys;//current number of keys stored in node
    private ArrayList<Integer> children;//A list of Child Nodes byteOffset pointers from RandomAccessFile
    private boolean isLeaf;//if node has no children or not
    private int byteOffset;//points to the first byte of the node from RandomAccessFile
    private int parentPointer;//points to the parent nodes byteOffset from RandomAccessFile
    
    /**@Constructor: for a new BTreeNode
     * @param degree
     * @param isLeaf 
     */
    public BTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.numKeys = 0;
        this.keys = new ArrayList<BTreeObject>();
        this.children = new ArrayList<Integer>();
        this.byteOffset = -1;
        this.parentPointer = -1;
    }

	/**
	 * @return the numKeys
	 */
	public int getNumKeys() {
		return numKeys;
	}

	/**
	 * @param numKeys the numKeys to set
	 */
	public void setNumKeys(int numKeys) {
		this.numKeys = numKeys;
	}

	
	/**
	 * Removes & returns Key from node
	 * @return the key at given index
	 */
	public BTreeObject removeKey(int index) {
		return keys.remove(index);
	}

	/**
	 * @param key to be inserted to node
	 */
	public void addKey(BTreeObject key) {
		this.keys.add(key);
	}
	/**
	 * @param key to be inserted to node
	 */
	public void addKey(BTreeObject key, int index) {
		this.keys.add(index,key);
	}
	/**
	 * @return a given key at index in node
	 * @param key index in node
	 */
	public BTreeObject getKey(int index) {
		return this.keys.get(index);
	}

	/**
	 * @return the isLeaf
	 */
	public boolean isLeaf() {
		return isLeaf;
	}

	/**
	 * @param isLeaf the isLeaf to set
	 */
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	/**
	 * @return the parentPointer
	 */
	public int getParentPointer() {
		return parentPointer;
	}

	/**
	 * @param parentPointer the parentPointer to set
	 */
	public void setParentPointer(int parentPointer) {
		this.parentPointer = parentPointer;
	}

	/**
	 * @return the byteOffset
	 */
	public int getByteOffset() {
		return byteOffset;
	}

	/**
	 * @param byteOffset the byteOffset to set
	 */
	public void setByteOffset(int byteOffset) {
		this.byteOffset = byteOffset;
	}

	/**
	 * Removes & returns childPointer from node
	 * @return the childPointer at given index
	 */
	public int removeChild(int index) {
		return children.remove(index);
	}

	/**
	 * @param childPointer to be inserted to node
	 */
	public void addChild(int childPointer) {
		this.children.add(childPointer);
	}
	/**
	 * @param childPointer to be inserted to node
	 */
	public void addChild(int childPointer,int index) {
		this.children.add(index,childPointer);
	}
	
	/**
	 * @return a given childPointer at index in node
	 * @param childPointer index in node
	 */
	public int getChild(int index) {
		return this.children.get(index);
	}

	@Override
	/**
	 * @return String of BTreeNode
	 */
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("keys: ");
        for(BTreeObject obj: keys) {
        	result.append(obj); result.append(" ");
        }
        result.append("\nchildren Pointers: ");
        for (int pointer:children)
        {
            result.append(pointer); result.append(" ");
        }
        return result.toString();
    }
    
}