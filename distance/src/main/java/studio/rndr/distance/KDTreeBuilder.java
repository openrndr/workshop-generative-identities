package studio.rndr.distance;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class KDTreeBuilder<T extends KDTreeItem> {

    public static <T extends KDTreeItem> KDTreeNode<T> insertItem(KDTreeNode<T> root, T item) {

        if (root.isLeaf()) {
            root.item = item;
            return root;
        }
        else {
            if (item.getValue(root.dimension) < root.median) {
                return insertItem(root.children[0], item);
            }
            else {
                return insertItem(root.children[1], item);
            }
        }
    }

    public static class ForkBuilder<T extends KDTreeItem> extends RecursiveAction {
        final KDTreeNode<T> root;
        final List<T> items;
        final int levels;

        public ForkBuilder(KDTreeNode<T> root, List<T> items, int levels) {
            this.root = root;
            this.items = items;
            this.levels = levels;
        }

        @Override
        protected void compute() {
            int dimensions = 0;

            if (items.size() > 0) {
                for (T item: items) {
                    dimensions = item.getDimensions();
                    break;
                }

                final int dimension = levels % dimensions;
                List<T> values = new ArrayList<T>();
                for (T item: items) {
                    values.add(item);
                }

                root.dimension = dimension;
                T median = QuickSelect.selectNth(items, items.size() / 2, item -> item.getValue(dimension));

                System.out.println(median.getValue(dimension));
                List<T> leftItems = new ArrayList<T>(items.size()/2);
                List<T> rightItems = new ArrayList<T>(items.size()/2);

                root.median = median.getValue(dimension);
                root.item = median;
                for (T item:items) {
                    if (item == median) {
                        continue;
                    }
                    if (item.getValue(dimension) < root.median) {
                        leftItems.add(item);
                    }
                    else {
                        rightItems.add(item);
                    }
                }


                List<ForkBuilder<T>> actions = new ArrayList<ForkBuilder<T>>();
                if (leftItems.size() > 0) {
                    root.children[0] = new KDTreeNode<T>();
                    root.children[0].parent = root;


                    if (leftItems.size() > 8) {
                        actions.add(new ForkBuilder<T>(root.children[0], leftItems, levels+1));
                    }
                    else {
                        buildTree(root.children[0],leftItems,levels+1);
                    }

                }
                if (rightItems.size() > 0) {
                    root.children[1] = new KDTreeNode<T>();
                    root.children[1].parent = root;
                    if (rightItems.size() > 8) {
                        actions.add( new ForkBuilder<T>(root.children[1], rightItems, levels+1));
                    }
                    else {
                        buildTree(root.children[1], rightItems, levels+1);
                    }
                }
                invokeAll(actions);
            }
        }
    }

    public static <T extends KDTreeItem> KDTreeNode<T> buildTreeParallel(KDTreeNode<T> root, List<T> items, int levels) {
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new ForkBuilder<T>(root,items,levels));
        return root;
    }

    public static <T extends KDTreeItem> KDTreeNode<T> buildTree(KDTreeNode<T> root, List<T> items, int levels) {
        int dimensions = 0;

        if (items.size() > 0) {
            for (T item: items) {
                dimensions = item.getDimensions();
                break;
            }

            final int dimension = levels % dimensions;
            List<T> values = new ArrayList<T>();
            for (T item: items) {
                values.add(item);
            }

            root.dimension = dimension;
            T median = QuickSelect.selectNth(items, items.size() / 2, item -> item.getValue(dimension));

            System.out.println(median.getValue(dimension));
            List<T> leftItems = new ArrayList<T>(items.size()/2);
            List<T> rightItems = new ArrayList<T>(items.size()/2);

            root.median = median.getValue(dimension);
            root.item = median;
            for (T item:items) {
                if (item == median) {
                    continue;
                }
                if (item.getValue(dimension) < root.median) {
                    leftItems.add(item);
                }
                else {
                    rightItems.add(item);
                }
            }

            if (leftItems.size() + rightItems.size() + 1 != items.size()) {
                System.out.println("BROKEND! " + leftItems.size() + " " + rightItems.size() + " " + items.size());
            }

            if (leftItems.size() > 0) {
                root.children[0] = new KDTreeNode<T>();
                root.children[0].parent = root;
                buildTree(root.children[0], leftItems, levels + 1);
            }
            if (rightItems.size() > 0) {
                root.children[1] = new KDTreeNode<T>();
                root.children[1].parent = root;
                buildTree(root.children[1], rightItems, levels + 1);
            }
        }
        return root;
    }
}
