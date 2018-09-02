package studio.rndr.distance;

import org.openrndr.math.Vector2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DirectionalDistanceField {

    public Vector2[][] field;

    int width;
    int height;

    static double clamp(double x, double min, double max) {
        return Math.max(min, Math.min(x, max));
    }
    static int clamp(int x, int min, int max) {
        return Math.max(min, Math.min(x, max));
    }


    public DirectionalDistanceField(int width, int height) {
        field = new Vector2[height][width];
        this.width = width;
        this.height = height;
    }

    public DirectionalDistanceField blur(int w) {
        DirectionalDistanceField result = new DirectionalDistanceField(width, height);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

                double n = 0;
                Vector2 sum = Vector2.Companion.getZERO();
                for (int j = -w; j <= w; ++j) {
                    for (int i = -w; i <= w; ++i) {
                        int fy = y + j;
                        int fx = x + i;

                        if (fy >= 0 && fx >= 0 && fy < height && fx < width) {
                            sum = sum.plus(field[fy][fx]);
                            n += 1;
                        }
                    }
                }
                result.field[y][x] = sum.times(1.0 / n);

            }
        }

        return result;
    }

    public static DirectionalDistanceField fromFile(File f) {
        try {
            FileInputStream fis = new FileInputStream(f);

            ByteBuffer dimensions = ByteBuffer.allocate(4 * 2);
            dimensions.order(ByteOrder.nativeOrder());
            fis.getChannel().read(dimensions);
            dimensions.rewind();
            int width = dimensions.getInt();
            int height = dimensions.getInt();

            DirectionalDistanceField df = new DirectionalDistanceField(width, height);

            ByteBuffer bb = ByteBuffer.allocate(width * height * 2 * 4);
            bb.order(ByteOrder.nativeOrder());

            fis.getChannel().read(bb);
            bb.rewind();

            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    float fx = bb.getFloat();
                    float fy = bb.getFloat();
                    df.field[y][x] = new Vector2(fx, fy);
                }
            }
            return df;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Vector2 distance(Vector2 point) {
        double x = clamp(point.getX(), 0, width - 1);
        double y = clamp(point.getY(), 0, height - 1);

        int x0 = (int) x;
        int y0 = (int) y;

        int x1 = Math.min(width - 1, x0 + 1);
        int y1 = Math.min(height - 1, y0 + 1);

        Vector2 d00 = distance(x0, y0);
        Vector2 d01 = distance(x0, y1);
        Vector2 d11 = distance(x1, y1);
        Vector2 d10 = distance(x1, y0);

        double fx = x - Math.ceil(x);
        double fy = y - Math.ceil(y);
        double ifx = 1.0 - fx;
        double ify = 1.0 - fy;

        return d00.times(ifx * ify)
                .plus(d01.times(ifx * fy))
                .plus(d11.times(fx * fy))
                .plus(d10.times(fx * ify));
    }

    public Vector2 distance(int x, int y) {
        return field[clamp(y, 0, height - 1)][clamp(x, 0, width - 1)];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}


