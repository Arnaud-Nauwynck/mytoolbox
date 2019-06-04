package fr.an.img.stegano.split.img;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.an.img.stegano.split.impl.Crc32Utils;
import lombok.AllArgsConstructor;

public class ToImagesOutputStream extends OutputStream {

    private Supplier<BufferedImage> imagesSupplier;
    
    @AllArgsConstructor
    public static class ImageDataFragment {
        public final BufferedImage img;
        public final int imageIndex;
        public final int encodedLen;
        public final long crc32;
    }
    
    private Consumer<ImageDataFragment> imagesConsumer;
    
    private int currentImageIndex = 1;
    
    private BufferedImage currentImage;
    private byte[] currentData;
    private int currentDataLen;
    private int currentDataMax;
    
    public ToImagesOutputStream(Supplier<BufferedImage> imagesSupplier,
            Consumer<ImageDataFragment> imagesConsumer) {
        this.imagesSupplier = imagesSupplier;
        this.imagesConsumer = imagesConsumer;
        allocImage();
    }

    private void allocImage() {
        currentImage = imagesSupplier.get();
        currentDataMax = Img3ByteUtils.lsb4BytesCountFor(currentImage);
        currentData = new byte[currentDataMax];
        currentDataLen = 0;
    }

    @Override
    public void write(int b) throws IOException {
        if (currentDataMax == 0) {
            allocImage();
        }
        currentData[currentDataLen++] = (byte) b;
        if (currentDataLen == currentDataMax) {
            flushImage();
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (currentDataMax == 0) {
            allocImage();
        }
        int currOff = off, currLen = len;
        for(;;) {
            int currCopyLen = Math.min(currLen, currentDataMax-currentDataLen);
            if (currCopyLen == 0) {
                break;
            }
            System.arraycopy(b, currOff, currentData, currentDataLen, currCopyLen);
            currentDataLen += currCopyLen;
            currOff += currCopyLen;
            currLen -= currCopyLen;
            if (currentDataLen == currentDataMax) {
                flushImage();
                if (currLen > 0) {
                    allocImage();
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public void flush() throws IOException {
        // flushImage .. cf close instead
        super.flush();
    }

    private void flushImage() {
        if (currentDataMax > 0) {
            // finish filling image with random values
            Random r = new Random();
            for(int i = currentDataLen; i < currentDataMax; i++) {
                currentData[i] = (byte) r.nextInt(255); // r.nextBytes(bytes);
            }
            // put data in image..
            Img3ByteUtils.putLsb4Bits(currentImage, currentData, currentDataLen);
            long crc32 = Crc32Utils.crc32(currentData, 0, currentDataLen);
            
            imagesConsumer.accept(new ImageDataFragment(currentImage, currentImageIndex++, currentDataLen, crc32));
            currentDataMax = 0;
        }
    }

    @Override
    public void close() throws IOException {
        flushImage();
        super.close();
    }
    
}
