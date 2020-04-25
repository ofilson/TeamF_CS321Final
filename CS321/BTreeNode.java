
public class BTreeNode{
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