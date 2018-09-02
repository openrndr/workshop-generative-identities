package studio.rndr.distance;

/**
 * Created with IntelliJ IDEA.
 * User: edwin
 * Date: 12/4/13
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface KDTreeItem {
    public int getDimensions();
    public double getValue(int dimension);
}
