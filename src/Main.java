/**
 * Kareem Mohamed Morsy, ID: 20190386, S1, Computer Science
 * Mohamed Ashraf Mohamed, ID : 20190424, S2, Computer Science
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 2 || args.length == 4) {
            File file = new File(args[1]);
            if (file.exists()) {
                if (args[0].toLowerCase().contains("c") && args.length == 4) { // Compress
                    BufferedImage img = ImageIO.read(file);
                    int width = img.getWidth();
                    int height = img.getHeight();
                    int[][] imgArr = new int[width][height];
                    Raster raster = img.getData();
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            imgArr[i][j] = raster.getSample(i, j, 0);
                        }
                    }

                    byte[] compressedBytes = compress(imgArr, Integer.parseInt(args[2]), Integer.parseInt(args[3]));

                    String newPath = file.getPath() + ".vq";
                    File compressedFile = new File(newPath);
                    compressedFile.createNewFile();
                    Files.write(compressedFile.toPath(), compressedBytes);
                } else if (args[0].toLowerCase().contains("d") && args.length == 2) { // Decompress
                    byte[] input = Files.readAllBytes(file.toPath());

                    BitSet compressedBits = BitSet.valueOf(input);

                    int[][] imgArr = decompress(compressedBits);

                    String newPath = file.getPath() + ".png";

                    BufferedImage outputImage = new BufferedImage(imgArr.length, imgArr[0].length, BufferedImage.TYPE_BYTE_GRAY);
                    WritableRaster raster = outputImage.getRaster();

                    int[] flatArray = new int[imgArr.length * imgArr[0].length];
                    for (int y = 0; y < imgArr[0].length; y++) {
                        for (int x = 0; x < imgArr.length; x++) {
                            flatArray[(y * imgArr[0].length) + x] = imgArr[x][y];
                        }
                    }

                    raster.setSamples(0, 0, imgArr.length, imgArr[0].length, 0, flatArray);

                    File decompressedFile = new File(newPath);
                    decompressedFile.createNewFile();

                    ImageIO.write(outputImage, "png", decompressedFile);
                } else {
                    System.out.println(args[0] + " is invalid argument");
                }
            } else {
                System.out.println(args[1] + " is not an existing file");
            }
        } else {
            System.out.println("No arguments were supplied");
            System.out.println("Examples: -c [filename] [vector size] [code book bit count]");
            System.out.println("Or: -d [filename]");
        }
    }

    private static int[][] decompress(BitSet compressedBits) {
        BitSet header = compressedBits.get(0, 128);
        long[] headerLongArr = header.toLongArray();
        long width = headerLongArr[0];
        long height = headerLongArr[1];
        int vectorSize = 0;
        int codeBookBits = 0;

        int[][] result = new int[(int) width][(int) height];

        BitSet codeBookBitsBitSet = compressedBits.get(128, 128 + 8);
        for (int i = 0; i < 8; i++) {
            codeBookBits |= codeBookBitsBitSet.get(i) ? 1 << i : 0;
        }

        BitSet vectorSizeBitSet = compressedBits.get(128 + 8, 128 + 8 + 32);
        for (int i = 0; i < 8; i++) {
            vectorSize |= vectorSizeBitSet.get(i) ? 1 << i : 0;
        }

        List<Matrix> codeBook = new ArrayList<>();
        BitSet codeBookBinary = compressedBits.get(
                128 + 8 + 32,
                128 + 8 + 32 + ((int) Math.pow(2, codeBookBits) * vectorSize * vectorSize * 8)
        );

        for (int i = 0; i < Math.pow(2, codeBookBits); i++) {
            Matrix vector = new Matrix(vectorSize, vectorSize);
            BitSet vectorBitSet = codeBookBinary.get(i * vectorSize * vectorSize * 8, (i + 1) * vectorSize * vectorSize * 8);

            for (int x = 0; x < vectorSize; x++) {
                for (int y = 0; y < vectorSize; y++) {
                    BitSet pixelBitSet = vectorBitSet.get((y + x * vectorSize) * 8, (y + 1 + x * vectorSize) * 8);
                    int pixel = 0;

                    for (int j = 0; j < 8; j++) {
                        pixel |= pixelBitSet.get(j) ? 1 << j : 0;
                    }

                    vector.setPixel(x, y, pixel);
                }
            }

            codeBook.add(vector);
        }

        BitSet imageBinary = compressedBits.get(
                128 + 8 + 32 + ((int) Math.pow(2, codeBookBits) * vectorSize * vectorSize * 8),
                128 + 8 + 32 + ((int) Math.pow(2, codeBookBits) * vectorSize * vectorSize * 8) +
                        (int)(codeBookBits * (width / vectorSize) * (height / vectorSize))
        );

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                BitSet codeBookIdxBitSet = imageBinary.get((int)(x * width + y) * codeBookBits, (int)(x * width + y + 1) * codeBookBits);

                int codeBookIdx = 0;
                System.out.println(codeBookIdx);
                for (int i = 0; i < codeBookBits; i++) {
                    codeBookIdx |= codeBookIdxBitSet.get(i + (int)(width * x + y)) ? 1 << i : 0;
                }

                for (int i = 0; i < vectorSize; i++) {
                    for (int j = 0; j < vectorSize; j++) {
                        int pixelX = (x * vectorSize) + i;
                        int pixelY = (y * vectorSize) + j;

                        if(pixelX < width && pixelY < height)
                            result[pixelX][pixelY] = codeBook.get(codeBookIdx).getPixel(i, j);
                    }
                }
            }
        }

        return result;
    }

    public static byte[] compress(int[][] image, int vectorSize, int codeBookBits) {
        Matrix imageMatrix = new Matrix(image.length, image[0].length);
        for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[x].length; y++) {
                imageMatrix.setPixel(x, y, image[x][y]);
            }
        }

        List<Matrix> imageVectors = imageMatrix.split(vectorSize);

        List<List<Matrix>> LBG = Matrix.splitLBG(codeBookBits, imageVectors, vectorSize);

        ArrayList<Matrix> averages = new ArrayList<>();

        for (List<Matrix> list : LBG) {
            averages.add(Matrix.getAverage(list, vectorSize, vectorSize));
        }

        ArrayList<Matrix>[] newLBG;

        while (true) {
            newLBG = new ArrayList[averages.size()];
            for (int i = 0; i < averages.size(); i++)
                newLBG[i] = new ArrayList<>();

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
            for (int i = 0; i < averages.size(); i++) {
                Matrix currentAverage = averages.get(i);
                Matrix newAverage = Matrix.getAverage(newLBG[i], vectorSize, vectorSize);
                if (!currentAverage.equals(newAverage)) flag = false;
                averages.set(i, newAverage);
            }
            if (flag) break;
        }

        // Width and Height
        long[] header = {imageMatrix.getWidth(), imageMatrix.getHeight()};
        BitSet headerBits = BitSet.valueOf(header);
        List<Boolean> compressedBits = new ArrayList<>(168);
        for (int i = 0; i < 128; i++) {
            compressedBits.add(headerBits.get(i));
        }

        // Code book bits
        for (int i = 0; i < 8; i++) {
            compressedBits.add(((codeBookBits >> i) & 1) == 1);
        }

        // VectorSize
        for (int i = 0; i < 32; i++) {
            compressedBits.add(((vectorSize >> i) & 1) == 1);
        }

        // Code book
        for (Matrix codeBook :
                averages) {
            for (int x = 0; x < codeBook.getWidth(); x++) {
                for (int y = 0; y < codeBook.getHeight(); y++) {
                    int pixel = codeBook.getPixel(x, y);
                    for (int i = 0; i < 8; i++) {
                        compressedBits.add(((pixel >> i) & 1) == 1);
                    }
                }
            }
        }

        // Image
        for (Matrix vector : imageVectors) {
            int averageIndex = -1;
            int distance = Integer.MAX_VALUE;
            for (int i = 0; i < averages.size(); i++) {
                int currentDistance = averages.get(i).getDistance(vector);
                if (distance > currentDistance) {
                    distance = currentDistance;
                    averageIndex = i;
                }
            }

            System.out.println(averageIndex);

            for (int i = 0; i < codeBookBits; i++) {
                compressedBits.add(((averageIndex >> i) & 1) == 1);
            }
        }

        return bitListToBytes(compressedBits);
    }

    private static byte[] bitListToBytes(List<Boolean> bits) {
        BitSet bitSet = new BitSet(bits.size());
        for (int i = 0; i < bits.size(); i++) {
            bitSet.set(i, bits.get(i));
        }

        byte[] bytes = bitSet.toByteArray();
        if (bytes.length * 8 >= bits.size()) {
            return bytes;
        } else {
            return Arrays.copyOf(bytes, bits.size() / 8 + (bits.size() % 8 == 0 ? 0 : 1));
        }
    }
}
