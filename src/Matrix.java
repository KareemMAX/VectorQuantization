import java.util.ArrayList;
import java.util.List;

public class Matrix {
    private final int[][] vector;
    private final int width;
    private final int height;

    public Matrix(int width, int height) {
        vector = new int[width][height];
        this.width = width;
        this.height = height;
    }

    public int getPixel(int x, int y) {
        if (x >= width) return 0;
        if (y >= height) return 0;

        return vector[x][y];
    }

    public void setPixel(int x, int y, int pixel) {
        vector[x][y] = pixel;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDistance(Matrix m) {
        int d = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                d += (getPixel(x, y) - m.getPixel(x, y)) * (getPixel(x,y) - m.getPixel(x, y));
            }
        }

        return d;
    }

    public List<Matrix> split(int vectorSize) {
        ArrayList<Matrix> vectors = new ArrayList<>();

        for (int x = 0; x < width; x+=vectorSize) {
            for (int y = 0; y < height; y+=vectorSize) {
                Matrix vector = new Matrix(vectorSize, vectorSize);

                for (int i = 0; i < vectorSize; i++) {
                    for (int j = 0; j < vectorSize; j++) {
                        vector.setPixel(i, j, getPixel(i + x, j + y));
                    }
                }
                vectors.add(vector);
            }
        }
        return vectors;
    }

    public static Matrix getAverage(List<Matrix> matrices) {
        Matrix result = new Matrix(matrices.get(0).width, matrices.get(0).height);

        for (Matrix m :
                matrices) {
            for (int x = 0; x < m.width; x++) {
                for (int y = 0; y < m.height; y++) {
                    result.setPixel(x, y, result.getPixel(x, y) + m.getPixel(x, y));
                }
            }
        }

        for (int x = 0; x < result.width; x++) {
            for (int y = 0; y < result.height; y++) {
                result.setPixel(x, y, result.getPixel(x, y) / matrices.size());
            }
        }

        return result;
    }
}
