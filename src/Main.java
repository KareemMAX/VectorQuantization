import java.util.ArrayList;
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

        List <List<Matrix>> LBG = Matrix.splitLBG(codeBlockBits, imageVectors);

        ArrayList <Matrix> averages = new ArrayList<>();

        for (List<Matrix> list : LBG){
            averages.add(Matrix.getAverage(list));
        }

        ArrayList<Matrix>[] newLBG;

        while (true){
            newLBG = new ArrayList[averages.size()];
            for (int i = 0; i < imageVectors.size(); i++) newLBG[i] = new ArrayList<>();
            for (Matrix imageVector : imageVectors) {
                int averageIndex = -1;
                int distance = Integer.MAX_VALUE;
                for (int j = 0; j < averages.size(); j++) {
                    int currentDistance = averages.get(j).getDistance(imageVector);
                    if (distance > currentDistance) {
                        distance = currentDistance;
                        averageIndex = j;
                    }
                }
                newLBG[averageIndex].add(imageVector);
            }

            boolean flag = true;
            for (int i = 0; i < averages.size(); i++){
                Matrix currentAverage = averages.get(i);
                Matrix newAverage = Matrix.getAverage(newLBG[i]);
                if (!currentAverage.equals(newAverage)) flag = false;
                averages.set(i, newAverage);
            }
            if (flag) break;
        }


        for (Matrix vector : imageVectors){
            int averageIndex = -1;
            int distance = Integer.MAX_VALUE;
            for (int i = 0; i < averages.size(); i++){
                int currentDistance = averages.get(i).getDistance(vector);
                if (distance > currentDistance){
                    distance = currentDistance;
                    averageIndex = i;
                }
            }

        }

        return null;
    }
}
