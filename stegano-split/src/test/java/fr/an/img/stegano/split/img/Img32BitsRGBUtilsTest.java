package fr.an.img.stegano.split.img;

import java.awt.image.BufferedImage;
import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class Img32BitsRGBUtilsTest {

    @Test
    public void testLsb4BytesCountFor() {
        final int w = 100, h = 100;
        BufferedImage img = Img32BitsRGBUtils.createRGB32Image(w, h);
        int lsbBytesCount = Img32BitsRGBUtils.lsb8BytesCountFor(img);
        Assert.assertEquals(w*h*3-8, lsbBytesCount);
        
        byte[] data = new byte[lsbBytesCount];
        for(int i = 0; i < lsbBytesCount; i++) {
            data[i] = (byte) i;
        }
        
        Img32BitsRGBUtils.putLsb8Bits(img, data, data.length);
        ImgUtils.saveImage(img, "bmp", new File("test.bmp"));
    }
    
    @Test
    public void testReload() {
        BufferedImage checkImg = ImgUtils.readImage(new File("test.bmp"));

        final int w = 100, h = 100;
        BufferedImage img = Img32BitsRGBUtils.createRGB32Image(w, h);
        int lsbBytesCount = Img32BitsRGBUtils.lsb8BytesCountFor(img);
        byte[] data = new byte[lsbBytesCount];
        for(int i = 0; i < lsbBytesCount; i++) {
            data[i] = (byte) i;
        }
        byte[] checkData = new byte[lsbBytesCount];
        Img32BitsRGBUtils.getLsb8Bits(checkData, checkImg);

        for(int i = 0; i < lsbBytesCount; i++) {
            if (data[i] != checkData[i]) {
                System.err.println("Failed [" + i + "] expected " + data[i] + ", got " + checkData[i]);
            }
        }
    }
}
