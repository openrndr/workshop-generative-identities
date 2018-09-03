package studio.rndr.distance;

import org.openrndr.color.ColorRGBa;
import org.openrndr.draw.ColorBufferShadow;
import org.openrndr.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edwin on 25/05/16.
 */
public class FastestDirectionalFieldBuilder {

    public interface Sampler {
        public int sample(ColorRGBa color);
    }

    public class DefaultSampler implements FastestDirectionalFieldBuilder.Sampler {
        @Override
        public int sample(ColorRGBa color) {
            return color.getR() < 0.5 ? 0 : 1;
        }
    }

    class TreeVector2 implements KDTreeItem {

        public Vector2 value;

        public TreeVector2(Vector2 v) {
            value = v;
        }

        @Override
        public int getDimensions() {
            return 2;
        }

        @Override
        public double getValue(int dimension) {
            if (dimension == 0) {
                return value.getX();
            } else if (dimension == 1) {
                return value.getY();
            } else {
                throw new RuntimeException("unsupported dimension");
            }
        }
    }

    public DirectionalDistanceField build(ColorBufferShadow shadow, Sampler sampler ) {

        ArrayList<Vector2> borderElements[] = new ArrayList[]{new ArrayList(), new ArrayList()};
        int width = shadow.getColorBuffer().getWidth();
        int height = shadow.getColorBuffer().getHeight();

        KDTreeBuilder builder = new KDTreeBuilder();
        KDTreeNode<TreeVector2> nodes[] = new KDTreeNode[2];

        nodes[0] = new KDTreeNode();
        nodes[1] = new KDTreeNode();

        DirectionalDistanceField ddf = new DirectionalDistanceField(width, height);
        int count = 0;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int current = sampler.sample(shadow.read(x, y));
                for (int j = -1; j <= 1; ++j)
                    for (int i = -1; i <= 1; ++i) {
                        int v = y + j;
                        int u = x + i;
                        if (u >= 0 && v >= 0 && v < height && u < width) {
                            int candidate = shadow.read(u, v).getR() < 0.5 ? 0 : 1;
                            if (candidate != current) {
                                count++;
                                borderElements[current].add(new Vector2(x, y));
                                break;
                            }
                        }
                    }
            }
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < 2; ++i) {
            List<TreeVector2> items = new ArrayList<>();
            borderElements[0].forEach(e -> {
                        items.add(new TreeVector2(e));
                    }
            );
            builder.buildTree(nodes[i], items, 0);
        }
        System.out.println("building tree: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        ArrayList<Integer> ys = new ArrayList<>();
        for (int y = 0; y < height; ++y) {
            ys.add(y);
        }

        ys.parallelStream().forEach(y -> {
            KDTreeNode<TreeVector2> nearest = null;
            for (int x = 0; x < width; ++x) {
                int current = shadow.read(x, y).getR() < 0.5 ? 0 : 1;
                KDTreeMutator m = new KDTreeMutator();
                m.findNearest(nodes[1 - current], new TreeVector2(new Vector2(x, y)));
                nearest = m.nearestArg;

                double dx = nearest.item.value.getX() - x;
                double dy = nearest.item.value.getY() - y;
                ddf.field[y][x] = new Vector2(dx, dy);
            }
        });
        System.out.println("ddf map: " + (System.currentTimeMillis() - start));
        return ddf;
    }
}