package studio.rndr.distance;

import java.util.*;

public class KDTreeMutator<T extends KDTreeItem> {

    public static <T extends KDTreeItem> double sqrDistance(T left, T right) {
        double distance = 0;
        int dims = left.getDimensions();

        for (int i = 0; i < dims; ++i) {
            double d = left.getValue(i) - right.getValue(i);
            distance += d * d;
        }
        return distance;
    }

    public static <T extends KDTreeItem> List<T> findAll(KDTreeNode<T> root) {
        Stack<KDTreeNode<T>> stack = new Stack<KDTreeNode<T>>();
        List<T> all = new ArrayList<T>();
        stack.empty();
        stack.push(root);
        while (!stack.isEmpty()) {
            KDTreeNode<T> node = stack.pop();
            if (node.item != null) {
                all.add(node.item);
            }

            if (node.children[0] != null  /*&&!visited.contains(node.children[0])*/) {
                stack.push(node.children[0]);
            }
            if (node.children[1] != null /*&& !visited.contains(node.children[1])*/) {
                stack.push(node.children[1]);
            }
        }
        return all;
    }

    public static <T extends KDTreeItem> List<KDTreeNode<T>> findAllNodes(KDTreeNode<T> root) {
        Stack<KDTreeNode<T>> stack = new Stack<KDTreeNode<T>>();
        List<KDTreeNode<T>> all = new ArrayList<KDTreeNode<T>>();
        stack.empty();
        stack.push(root);
        while (!stack.isEmpty()) {
            KDTreeNode<T> node = stack.pop();
            if (node.item != null) {
                all.add(node);
            }

            if (node.children[0] != null  /*&&!visited.contains(node.children[0])*/) {
                stack.push(node.children[0]);
            }
            if (node.children[1] != null /*&& !visited.contains(node.children[1])*/) {
                stack.push(node.children[1]);
            }
        }
        return all;
    }


    public static <T extends KDTreeItem> KDTreeNode<T> insert(KDTreeNode<T> root, T item) {
        Stack<KDTreeNode<T>> stack = new Stack<KDTreeNode<T>>();
        stack.push(root);

        dive:
        while (true) {

            KDTreeNode<T> node = stack.peek();

            if (item.getValue(node.dimension) < node.median) {
                if (node.children[0] != null) {
                    stack.push(node.children[0]);
                } else {
                    // sit here
                    node.children[0] = new KDTreeNode<T>();
                    node.children[0].item = item;
                    node.children[0].dimension = (node.dimension + 1) % item.getDimensions();
                    node.children[0].median = item.getValue(node.children[0].dimension);
                    node.children[0].parent = node;
                    return node.children[0];
                }
            } else {
                if (node.children[1] != null) {
                    stack.push(node.children[1]);
                } else {
                    // sit here
                    node.children[1] = new KDTreeNode<T>();
                    node.children[1].item = item;
                    node.children[1].dimension = (node.dimension + 1) % item.getDimensions();
                    node.children[1].median = item.getValue(node.children[1].dimension);
                    node.children[1].parent = node;
                    return node.children[1];

                }
            }
        }
    }

    public static <T extends KDTreeItem> KDTreeNode<T> remove(KDTreeNode<T> toRemove) {
        // trivial case
        if (toRemove.isLeaf()) {

            if (toRemove.parent != null) {
                if (toRemove.parent.children[0] == toRemove) {
                    toRemove.parent.children[0] = null;
                } else if (toRemove.parent.children[1] == toRemove) {
                    toRemove.parent.children[1] = null;
                } else {
                    // broken!
                }

            } else {
                toRemove.item = null;
            }
        } else {
            Stack<KDTreeNode<T>> stack = new Stack<KDTreeNode<T>>();

            int branch = 0;

            if (toRemove.children[0] != null) {
                stack.push(toRemove.children[0]);
                branch = 0;
            } else {
                stack.push(toRemove.children[1]);
                branch = 1;
            }

            Double minValue = Double.POSITIVE_INFINITY;
            Double maxValue = Double.NEGATIVE_INFINITY;
            KDTreeNode<T> minArg = null;
            KDTreeNode<T> maxArg = null;

            while (!stack.isEmpty()) {
                KDTreeNode<T> node = stack.pop();

                if (node == null) {
                    throw new RuntimeException("null on stack");
                }

                if (node.item.getValue(toRemove.dimension) < minValue) {
                    minValue = node.item.getValue(toRemove.dimension);
                    minArg = node;
                }

                if (node.item.getValue(toRemove.dimension) > maxValue) {
                    maxValue = node.item.getValue(toRemove.dimension);
                    maxArg = node;
                }

                if (node.dimension != toRemove.dimension) {
                    if (node.children[0] != null) {
                        stack.push(node.children[0]);
                    }
                    if (node.children[1] != null) {
                        stack.push(node.children[1]);
                    }
                } else {
                    if (branch == 1) {
                        if (node.children[0] != null) {
                            stack.push(node.children[0]);
                        } else {
                            if (node.children[1] != null) {
                                stack.push(node.children[1]);
                            }
                        }
                    }
                    if (branch == 0) {
                        if (node.children[1] != null) {
                            stack.push(node.children[1]);
                        } else {
                            if (node.children[0] != null) {
                                stack.push(node.children[0]);
                            }
                        }
                    }
                }
            }


            if (branch == 1) {
                toRemove.item = minArg.item;
                toRemove.median = minArg.item.getValue(toRemove.dimension);
                remove(minArg);
            }
            if (branch == 0) {
                toRemove.item = maxArg.item;
                toRemove.median = maxArg.item.getValue(toRemove.dimension);
                remove(maxArg);
            }
        }
        return null;
    }

    double nearest = Double.POSITIVE_INFINITY;
    KDTreeNode nearestArg = null;

    public void findNearest(KDTreeNode<T> node, T item) {
        if (node == null) {
            return;
        }
        int route = -1;

        if (item.getValue(node.dimension) < node.median) {
            findNearest(node.children[0], item);
            route = 0;

        } else {
            findNearest(node.children[1], item);
            route = 1;
        }

        double distance = sqrDistance(item, node.item);
        if (distance < nearest) {
            nearest = distance;
            nearestArg = node;
        }


        if (route != -1) {
            double d = Math.abs(node.median - item.getValue(node.dimension));
            if (d * d < nearest) {
                findNearest(node.children[1 - route], item);
            }
        }

    }
}
