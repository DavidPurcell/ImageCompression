import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.io.*;
import javax.imageio.ImageIO;
import Jama.*;
import java.util.Scanner;

public class Project2 {
    //A = U * S * V'
    static double[][] arrayAlpha, arrayRed, arrayGreen, arrayBlue;
            
    public static double[][] Ak(double[][] u, double[][] v, double[] s, int k) {
        double[][] ak = new double[arrayRed.length][arrayRed[0].length];
        for(int i=0; i<ak.length;i++) {
            for(int q=0; q<ak[0].length;q++) {
                ak[i][q] = 0;
            }
        }
        int count = 0;
        for (int j = 0; j<k; j++) {
            double[][] temp = new double[arrayRed.length][arrayRed[0].length];
            for(int i=0; i<temp.length; i++) {
                //1 - 300
                for(int l=0; l<temp[0].length; l++) {
                    //1 - 200
                    temp[i][l] = 0;
                    temp[i][l] += u[i][j] * v[l][j] * s[j];
                }    
            }
            for(int i=0; i<ak.length;i++) {
                for(int q=0; q<ak[0].length;q++) {
                    ak[i][q] += temp[i][q];
                    if(ak[i][q] > 255) {
                        ak[i][q] = 255;
                    }
                }
            }
        }
        for(int i=0; i<ak.length;i++) {
            for(int q=0; q<ak[0].length;q++) {
                if(ak[i][q] > 255) {
                    ak[i][q] = 255;
                }
            }
        }
        return ak;
    }
    
    public static void imageToMatrix(String imageName) {
        BufferedImage buff;
        try {
            buff = ImageIO.read(new File(imageName));
            System.out.println("File accepted");
            arrayAlpha = new double[buff.getWidth()][buff.getHeight()];
            arrayRed = new double[buff.getWidth()][buff.getHeight()];
            arrayGreen = new double[buff.getWidth()][buff.getHeight()];
            arrayBlue = new double[buff.getWidth()][buff.getHeight()];
        
            for (int x = 0; x < arrayRed.length; x++) {
                for (int y = 0; y < arrayRed[x].length; y++) {
                    int p = buff.getRGB(x, y);
                    arrayAlpha[x][y] = p >> 24 & 0xFF;
                    arrayRed[x][y] = p >> 16 & 0xFF;
                    arrayGreen[x][y] = p >> 8 & 0xFF;
                    arrayBlue[x][y] = p & 0xFF;
                }
            }
        }
        catch (Exception e) {
            System.err.println("Invalid File");
            System.exit(0);
        }
    }
    
    public static void displayImage(double[][] alpha, double[][] red, double[][] green, double[][] blue) {
        BufferedImage buff = new BufferedImage(arrayRed.length, arrayRed[0].length, BufferedImage.TYPE_INT_RGB);
        JFrame frame = new JFrame("Your Picture!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        for (int x = 0; x < arrayRed.length; x++) {
            for (int y = 0; y < arrayRed[x].length; y++) {
                int value = (int) alpha[x][y] << 24 | (int) red[x][y] << 16 | (int) green[x][y] << 8 | (int) blue[x][y];
                buff.setRGB(x,y,value);
            }
        }
        
        frame.add(new JLabel(new ImageIcon(buff)));
        frame.pack();
        frame.setVisible(true);       
    }
    
    public static void displayImage(double[][] pixels) {
        BufferedImage buff = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
        JFrame frame = new JFrame("Your Picture!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[x].length; y++) {
                int value = (int) pixels[x][y];
                buff.setRGB(x,y,value);
            }
        }
        
        frame.add(new JLabel(new ImageIcon(buff)));
        frame.pack();
        frame.setVisible(true);       
    }
    
    public static double[][] getAK(double[][] array, int k) {
        Matrix m = new Matrix(array);
        SingularValueDecomposition svd = new SingularValueDecomposition(m);
        double[][] u = svd.getU().getArray();
        double[][] v = svd.getV().getArray();
        double[] s = svd.getSingularValues();     
        int rank = svd.rank();
        double[][] ak = Ak(u,v,s,k);
        System.out.println("First singular value: " + s[0]);
        System.out.println("Singular value k+1: " + (array[0][0] - ak[0][0]));
        return ak;
    }
    
    public static void imageStuff(String imageName, int k) {
        imageToMatrix(imageName);
        
        double[][] akAlpha = getAK(arrayAlpha, k);
        double[][] akRed = getAK(arrayRed, k);
        double[][] akGreen = getAK(arrayGreen, k);
        double[][] akBlue = getAK(arrayBlue, k);
        
        displayImage(akAlpha, akRed, akGreen, akBlue);
    }
    
    public static void pixelsToColors(double[][] pixels) {
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[x].length; y++) {
                int p = (int) pixels[x][y];
                arrayAlpha[x][y] = p >> 24 & 0xFF;
                arrayRed[x][y] = p >> 16 & 0xFF;
                arrayGreen[x][y] = p >> 8 & 0xFF;
                arrayBlue[x][y] = p & 0xFF;
            }
        }
    }
    
    public static void main(String[] args) {
        int k=0;
        int input = -1;
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter 1 for image compression 2 for matrix display 3 for image to matrix");
        input = scan.nextInt();
        if (input == 1) {
            System.out.println("Enter the name of your image");
            String imageName = scan.next();
            while(k!=-1) {
                System.out.println("Enter the k value to use for this image");
                k = scan.nextInt();
                imageStuff(imageName, k);
            }
        } else if (input == 2) {
            System.out.println("Enter the name of your csv file");
            try{
                String fileName = scan.next();
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String readLine = reader.readLine();
                double[][] pixels = new double[300][200];
                int i=0;
                while(readLine != null) {
                    String[] temp = readLine.split(",");
                    for (int j=0; j<temp.length; j++) {
                        pixels[i][j] = Integer.parseInt(temp[j]);
                    }
                    readLine = reader.readLine();
                    i++;
                }
                reader.close();
                displayImage(pixels);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (input == 3) {
            System.out.println("Generating matrix for specified image");
            String fileName = scan.next();
            imageToMatrix(fileName);
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".txt"));
                for (int i=0; i<arrayRed.length; i++) {
                    for (int j=0; j<arrayRed[i].length; j++) {
                        int value = (int) arrayAlpha[i][j] << 24 | (int) arrayRed[i][j] << 16 | (int) arrayGreen[i][j] << 8 | (int) arrayBlue[i][j];
                        writer.write(value + ",");
                    }
                    writer.write("\n");
                }
            } catch (Exception e) {
                System.out.println("Stuff broke");
            }
        }
    }
}