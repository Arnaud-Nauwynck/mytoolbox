package fr.an.img.stegano.split.img;

import java.awt.image.BufferedImage;
import java.io.File;

import org.junit.Test;

public class ImgUtilsTest {

//    @Test
//    public void testDummyDrawLsbInImage() {
//        BufferedImage img = Img3ByteUtils.create3ByteBGRImage(1000, 1000);
//        Img3ByteUtils.dummyDrawLsbInImage(img);
//        ImgUtils.saveImage(img, "bmp", new File("test1.bmp"));
//    }

    @Test
    public void testDrawInImage() {
        BufferedImage img = Img3ByteUtils.create3ByteBGRImage(1000, 1000);
        ImgUtils.dummyDrawStringInImage(img);
        ImgUtils.saveImage(img, "bmp", new File("testHello.bmp"));
    }

}
