package fr.an.img.stegano.split.img;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImgUtils
{
    public static void dummyDrawStringInImage(BufferedImage img) {
        dummyDrawStringInImage(img, "test painting java Text in java.awt.BufferedImage ..");
    }
    
    public static void dummyDrawStringInImage(BufferedImage img, String text) {
        Graphics2D gc = img.createGraphics();
        Font font = new Font("Arial", 0, 32);
        gc.setFont(font);
        int y = 100;
        for (int i = 0; i < 10; i++) {
            gc.drawString(text, 100, y);
            y += 120; // font.get
        }
    }

    public static void saveImage(BufferedImage src, String ext, File dest) {
        try {
            boolean written = ImageIO.write(src, ext, dest);
            if (!written) {
                throw new RuntimeException();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write file " + dest, ex);
        }
    }
    
    public static BufferedImage readImage(File imgFile) {
        BufferedImage img;
        try {
            img = ImageIO.read(imgFile);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read file " + imgFile, ex);
        }
        return img;
    }

    public static void copyImage(BufferedImage dest, BufferedImage source) {
        Graphics g = dest.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
    }
}
