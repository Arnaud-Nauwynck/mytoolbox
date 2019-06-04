package fr.an.img.stegano.split.img;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

import fr.an.img.stegano.split.impl.Crc32Utils;

public class Img32BitsRGBUtils {

    private static final boolean DEBUG = false;
    private static final boolean DEBUG_HEADER = false;
    private static final boolean DEBUG_SUMMARY = false;

    private static final int MASK_INT_000F = 0x000F;
    private static final int MASK_INT_FFF0 = 0xFFF0;
//    private static final int MASK_INT_00FF = 0x00FF;
//    private static final int MASK_INT_FF00 = 0xFF00;
    
    public static BufferedImage createRGB32Image(int w, int h) {
	    int transferType = DataBuffer.TYPE_INT;
	    ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), 
			  false, false, Transparency.OPAQUE, transferType);
	    WritableRaster raster = colorModel.createCompatibleWritableRaster(w, h);
	    return new BufferedImage(colorModel, raster, false, null);
    }

    public static int lsb8BytesCountFor(BufferedImage dest) {
        final int w = dest.getWidth(), h = dest.getHeight();
        final int dataCount = w * h * 3
                - 8;  // for encoding length, crc32
        return dataCount;
    }

    public static void putLsb8Bits(BufferedImage dest, byte[] encodeIn, int encodedLen) {
    	DataBufferInt dataBuffer = (DataBufferInt) dest.getRaster().getDataBuffer();
        int[] destData = dataBuffer.getData();
        final int w = dest.getWidth(), h = dest.getHeight();
        final int imgDataCount = w * h * 3;
        final long crc32 = Crc32Utils.crc32(encodeIn, 0, encodedLen);
        if (DEBUG_HEADER) {
            System.out.println(" length to encode:" + encodedLen + " crc32:" + crc32 + " from " + w + "x" + h + "*rgb=" + imgDataCount);
        }
        final int maxIdx = imgDataCount;
        int encodeIdx = 0;
        { // encode "encodedLen"
            int bitsForEncodedLen = encodedLen;
            for (int idx = 0; idx < 4; idx++) {
                int lsb = (bitsForEncodedLen >> 24) & MASK_INT_000F;
                bitsForEncodedLen = bitsForEncodedLen << 8;
                int prev = destData[idx];
                destData[idx] = withLsb8Bits(destData[idx], lsb);
                if (DEBUG_HEADER) System.out.println(" lsb8 len[" + idx + "] " + prev + " |& " + lsb + "=" + destData[idx]);
            }
        }
        { // encode "crc32"
            int bitsForCrc32 = (int) crc32;
            for (int idx = 4; idx < 8; idx++) {
                int lsb = (bitsForCrc32  >>> 24 ) & MASK_INT_000F;
                bitsForCrc32 = bitsForCrc32 << 8;
                int prev = destData[idx];
                destData[idx] = withLsb8Bits(destData[idx], lsb);
                if (DEBUG_HEADER) System.out.println(" lsb8 crc32[" + idx + "] " + prev + " |& " + lsb + "=" + destData[idx]);
            }
        }

        for(int idx = 8; idx < maxIdx; idx++) {
            if (encodeIdx >= encodedLen-5) {
                debug();
            }
            int lsb = encodeIn[encodeIdx++];
            int prev = destData[idx];
            destData[idx] = withLsb8Bits(destData[idx], lsb);
            if (DEBUG_HEADER) {
                if (encodeIdx < 6 || encodeIdx >= encodedLen-6) {
                    System.out.println(" lsb4[" + idx + "] " + prev + " |& " + lsb + "=" + destData[idx]);
                }
            }
        }
        
        final int imgBytes = w * h * 3;
        if (DEBUG_SUMMARY) {
            System.out.println(" done encoded"
                + " encodedLen: " + encodedLen + " / " + w + "x" + h + "*rgb=" + imgBytes 
                + " crc32:" + Crc32Utils.crc32(encodeIn, 0, encodedLen)
                + " [0]:" + encodeIn[0] + "," + encodeIn[1] + "," + encodeIn[2] + ".." 
                + " " + encodeIn[encodedLen-3] + "," +encodeIn[encodedLen-2] + "," + encodeIn[encodedLen-1]
                );
        }
    }

    public static int getLsb8Bits(byte[] decodeOut, BufferedImage img) {
    	DataBufferInt dataBuffer = (DataBufferInt) img.getRaster().getDataBuffer();
        int[] imgData = dataBuffer.getData();
        final int w = img.getWidth(), h = img.getHeight();
        final int bytesCount = w * h * 3;

        int resDecodedLen = 0;
        {
            // decode "encodeLen"
            for(int idx = 0; idx < 4; idx++) {
                int lsb = imgData[idx] & MASK_INT_000F;
                resDecodedLen = (resDecodedLen << 8) | lsb;
                if (DEBUG_HEADER) 
                    System.out.println(" lsb8 len[" + idx + "] .. &| " + lsb + " = " + imgData[idx]);
            }
        }
        
        final long resDecodedCrc32;
        {
            int crc32Bits = 0;
            // decode "encodeLen"
            for(int idx = 4; idx < 8; idx++) {
                int lsb = imgData[idx] & MASK_INT_000F;
                crc32Bits = (crc32Bits << 8) | lsb;
                if (DEBUG_HEADER) 
                    System.out.println(" lsb8 crc32[" + idx + "] .. &| " + lsb + " = " + imgData[idx]);
            }
            resDecodedCrc32 = crc32Bits;
        }
        if (DEBUG_HEADER) System.out.println(" length to decode:" + resDecodedLen + " crc32:" + resDecodedCrc32 + " from " + w + "x" + h + "*rgb=" + bytesCount );
        
        final int maxIdx = bytesCount; // - bytesCount % 8;
        int decodeIdx = 0;
        for(int idx = 8; idx < maxIdx; idx++) {
            if (decodeIdx >= resDecodedLen-5) {
                debug();
            }

            int lsb = imgData[idx] & MASK_INT_000F;
            decodeOut[decodeIdx] = (byte) lsb;
            if (DEBUG) {
                if (decodeIdx < 6 || decodeIdx >= resDecodedLen-6) {
                    System.out.println(" decoded[" + decodeIdx + "]:" + decodeOut[decodeIdx]);
                }
            }
                        
            decodeIdx++;
            if (decodeIdx >= resDecodedLen) {
                break;
            }
        }
        
        long checkCrc32 = Crc32Utils.crc32(decodeOut, 0, resDecodedLen);
        if ((int)resDecodedCrc32 != (int)checkCrc32) {
            System.err.println("**** mismatch crc32 ! " + resDecodedCrc32 + " != recomputed " + checkCrc32);
        }
//        if (resDecodedCrc32 != checkCrc32) {
//            System.err.println("**** mismatch (signed?) crc32 ! " + resDecodedCrc32 + " != recomputed " + checkCrc32);
//        }
        if (DEBUG_SUMMARY) {
            System.out.println("  decodedLen: " + resDecodedLen 
                + " crc32: " + checkCrc32
                + " [0]:" + decodeOut[0] + "," + decodeOut[1] + "," + decodeOut[2] + ".."
                + decodeOut[resDecodedLen - 3] + "," +decodeOut[resDecodedLen - 2] + "," + decodeOut[resDecodedLen - 1]
                );
        }
        
        return resDecodedLen;
    }
    
    private static int withLsb8Bits(int value, int lsb) {
        return (value & MASK_INT_FFF0) | (MASK_INT_000F & lsb);
    }
    
    private static int debug() {
        int debug = 0;
        debug++;
        return debug;
    }

}
