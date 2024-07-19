import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoEditor {
    public static void convertToGrayscale(String str) throws IOException {
        BufferedImage img = null;
        File f = null;

        // read image
        String pathName = "src/input/" + str;
        try {
            f = new File(pathName);
            img = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println(e);
            return;
        }

        // get image's width and height
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = img.getRGB(0, 0, width, height, null, 0, width);

        // convert to grayscale
        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            int a = (p >> 24) & 0xff;
            int r = (p >> 16) & 0xff;
            int g = (p >> 8) & 0xff;
            int b = p & 0xff;
            int avg = (r + g + b) / 3;
            p = (a << 24) | (avg << 16) | (avg << 8) | avg;
            pixels[i] = p;
        }
        img.setRGB(0, 0, width, height, pixels, 0, width);

        // write image
        int dotIndex = str.lastIndexOf('.');
        String fileName = str.substring(0, dotIndex);
        String outputPathName = "src/output/" + fileName + "_grayscale.png";
        try {
            f = new File(outputPathName);
            ImageIO.write(img, "png", f);
            System.out.println("Converted to grayscale successfully");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void mirrorImage(String str) throws IOException {
        BufferedImage simg = null;
        File f = null;
        String pathName = "src/input/" + str;

        try {
            f = new File(pathName);
            simg = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println("Error: " + e);
            return;
        }

        int width = simg.getWidth();
        int height = simg.getHeight();
        BufferedImage mimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int lx = 0, rx = width - 1; lx < width; lx++, rx--) {
                int p = simg.getRGB(lx, y);
                mimg.setRGB(rx, y, p);
            }
        }

        int dotIndex = str.lastIndexOf('.');
        String fileName = str.substring(0, dotIndex);
        String outputPathName = "src/output/" + fileName + "_mirror.png";
        try {
            f = new File(outputPathName);
            ImageIO.write(mimg, "png", f);
            System.out.println("Mirror image created successfully");
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public static void compressImage(String str) throws IOException {
        String inputImagePath = "src/input/" + str;
        String outputFolderPath = "src/output/";
        int dotIndex = str.lastIndexOf('.');
        String fileName = str.substring(0, dotIndex);
        String outputFileName = fileName + "_compressed.png";
        float compressionQuality = 0.5f;

        try {
            BufferedImage originalImage = ImageIO.read(new File(inputImagePath));
            File outputFolder = new File(outputFolderPath);
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }
            compressAndSaveImage(originalImage, outputFolderPath + outputFileName, compressionQuality);
            System.out.println("Image compression successful.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compressAndSaveImage(BufferedImage originalImage, String outputPath, float quality) throws IOException {
        String formatName = outputPath.substring(outputPath.lastIndexOf(".") + 1);
        BufferedImage compressedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        compressedImage.createGraphics().drawImage(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
        ImageIO.write(compressedImage, formatName, new File(outputPath));
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("-------------------------");
        System.out.println("JAVA IMAGE EDITING APP");
        System.out.println("-------------------------");
        System.out.println("ENTER THE NAME OF THE IMAGE");
        String str = sc.next();

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(3);

            executorService.submit(() -> {
                try {
                    convertToGrayscale(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                try {
                    mirrorImage(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                try {
                    compressImage(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.shutdown();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            sc.close();
        }
    }
}
