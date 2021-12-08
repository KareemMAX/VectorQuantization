/**
 * Kareem Mohamed Morsy, ID: 20190386, S1, Computer Science
 * Mohamed Ashraf Mohamed, ID : 20190424, S2, Computer Science
 */

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

    public int[][] getVector() {
        return vector;
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

    public boolean equals(Matrix m){
        if (m.width != width) return false;
        if (m.height != height) return false;
        for (int i = 0; i < m.width; i++){
            for (int j = 0; j < m.height; j++){
                if (m.getPixel(i, j) != getPixel(i, j)) return false;
            }
        }
        return true;
    }

    public static Matrix getAverage(List<Matrix> matrices, int width, int height) {
        Matrix result = new Matrix(width, height);

        if (matrices.size() == 0) return result;

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

    public static List<List<Matrix>> splitLBG(int height, List<Matrix> matrices, int vectorSize){
        ArrayList < List <Matrix>> result = new ArrayList<>();

        if (height == 0){
            result.add(matrices);
            return result;
        }


        ArrayList <Matrix> larger = new ArrayList<>();
        ArrayList <Matrix> smaller = new ArrayList<>();

        Matrix avgFirst = Matrix.getAverage(matrices, vectorSize, vectorSize);
        Matrix avgSecond = Matrix.getAverage(matrices, vectorSize, vectorSize);

        for (int i = 0; i < avgSecond.width; i++){
            for (int j = 0; j < avgSecond.height; j++){
                avgSecond.setPixel(i, j, avgSecond.getPixel(i, j) + 1);
            }
        }

        for (Matrix matrix :
                matrices) {
            if (matrix.getDistance(avgFirst) > matrix.getDistance(avgSecond)){
                larger.add(matrix);
            }
            else {
                smaller.add(matrix);
            }
        }


        result.addAll(splitLBG(height - 1, smaller, vectorSize));
        result.addAll(splitLBG(height - 1, larger, vectorSize));


        return result;
    }
}
