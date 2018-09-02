package studio.rndr.distance;


import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: edwin
 * Date: 12/4/13
 * Time: 7:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class KDTreeNode<T extends KDTreeItem> {

    @Override
    public String toString() {
        return "KDTreeNode{" +
                "median=" + median +
                ", dimension=" + dimension +
                ", children=" + Arrays.toString(children) +
                ", item=" + item +
                "} " + super.toString();
    }

    public KDTreeNode<T> parent;
    public double median;
    public int dimension;
    public KDTreeNode<T> children[] = new KDTreeNode[2];
    public T item;


    //public Set<T> items = new HashSet<T>();
    boolean isLeaf() {
        return children[0] == null && children[1] == null;
    }

}
