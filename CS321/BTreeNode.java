import java.util.ArrayList;
import java.util.LinkedList;
/**
 * This class represents a BTreeNode to be stored in BTree
 * -----FUCNTIONALITY-----
 * stores BBTreeObjects as keys in an array list
 * has reference to a parent node by a pointer from the parents Integer byteOffset from the RandomAccessFile
 * also has reference to an arraylist of children node pointers same as parent pointer
 * ------------------------
 * @author MichaelKinsy, Oscar Filson, , ,
 *
 */
public class BTreeNode{
	 private int Parent;
	 private LinkedList<BTreeObject> keys;
	 private LinkedList<Integer> children;
	 private int numKeys;
	 private int offset;
	 private boolean isLeaf;
	 
	 
	 public BTreeNode() {
		 keys = new LinkedList<BTreeObject>();
		 children = new LinkedList<Integer>();
		 numKeys = 0;
		 Parent = -1;
	 }
	 
	 public int getParent() {
		 return Parent;
	 }
	 public void setParent(int Parent) {
		 this.Parent = Parent;
	 }
	 public int getNumKeys() {
		 return numKeys;
	 }
	 public void setNumKeys(int numKeys) {
		 this.numKeys = numKeys;
	 }
	 public int getOffset() {
		 return offset;
	 }
	 public void setOffset(int offset) {
		 this.offset = offset;
	 }
	 public BTreeObject getKey(int i) {
		 BTreeObject obj = keys.get(i);
		 return obj;
	 }
	 public void addKey(BTreeObject obj) {
		 keys.add(obj);
	 }
	 public void addKey(BTreeObject obj, int i) {
		 keys.add(i, obj);
	 }
	 public BTreeObject removeKey(int i) {
		 return keys.remove(i);
	 }
	 public LinkedList<BTreeObject> getKeys() {
		 return keys;
	 }
	 public int getChild(int i) {
		 return children.get(i).intValue();
	 }
	 public void addChild(int i) {
		 children.add(i);
	 }
	 public void addChild(Integer c, int i) {
		 children.add(i, c);
	 }
	 public int removeChild(int i) {
		 return children.remove(i);
	 }
	 public LinkedList<Integer> getChildren() {
		 return children;
	 }
	 public boolean isLeaf() {
		 return isLeaf;
	 }
	 public void setIsLeaf(boolean isLeaf) {
		 this.isLeaf = isLeaf;
	 }
	 public String toString() {
		 String s = new String();
		 s += "Keys: ";
		 for (int i = 0; i < keys.size(); i++) {
			 s += (keys.get(i) + " ");
		 }
		 s += "\nchildren: ";
		 for (int i = 0; i < children.size(); i++) {
			 s += (children.get(i) + " ");
		 }
		 return s;
	 }
    
}