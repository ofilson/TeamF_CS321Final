
public class BTreeNode{
    /**@InstanceVariables: for BTreeNodes*/
    private long key;
    private int min_degree;
    private BTreeNode left;
    private BTreeNode right;
    
    /**@Constructor: for BTreeNode*/
    public BTreeNode(int min_degree, long key) {
        this.min_degree = min_degree;
        this.key = key;
        this.right = null;
        this.left = null;
    }
}