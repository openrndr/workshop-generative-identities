package studio.rndr.distance;

import java.util.List;

public class QuickSelect {
    public static interface Mapper<T> {
        double map(T item);
    }

    public static <T> T selectNth(List<T> items, int n, Mapper<T> mapper) {
        int from = 0;
        int to = items.size()-1;

        // if from == to we reached the kth element
        while (from < to) {
            int r = from, w = to;
            double mid = mapper.map(items.get((r + w) / 2));

            // stop if the reader and writer meets
            while (r < w) {
                if (mapper.map(items.get(r)) >= mid) { // put the large values at the end
                    T tmp = items.get(w);
                    items.set(w,items.get(r));
                    items.set(r, tmp);
                    w--;
                } else { // the value is smaller than the pivot, skip
                    r++;
                }
            }

            // if we stepped up (r++) we need to step one down
            if (mapper.map(items.get(r)) > mid)
                r--;

            // the r pointer is on the end of the first k elements
            if (n <= r) {
                to = r;
            } else {
                from = r + 1;
            }
        }
        return items.get(n);
    }
}
