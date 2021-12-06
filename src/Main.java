import java.util.List;

public class Main {
    public static void main(String[] args) {

    }

    public static byte[] compress(int[][] image, int vectorSize, int codeBlockBits) {
        Matrix imageMatrix = new Matrix(image.length, image[0].length);
        for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[x].length; y++) {
                imageMatrix.setPixel(x, y, image[x][y]);
            }
        }

        List<Matrix> imageVectors = imageMatrix.split(vectorSize);
        Matrix avg = Matrix.getAverage(imageVectors);

        return null;
    }
}
