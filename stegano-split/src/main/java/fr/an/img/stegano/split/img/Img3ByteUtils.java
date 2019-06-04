package fr.an.img.stegano.split.img;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import fr.an.img.stegano.split.impl.Crc32Utils;

public class Img3ByteUtils {

    private static final boolean DEBUG = false;
    private static final boolean DEBUG_HEADER = false;
    private static final boolean DEBUG_SUMMARY = false;
    
    private static final int CST4 = 4;
    private static final byte MASK_BYTE_LSB4 = (byte) 0x0F;
    private static final byte MASK_BYTE_NOT_LSB4 = (byte) 0xF0;

    private static final int MASK_INT_LSB4 = 0x000F;
    private static final int MASK_INT_00F0 = 0x00F0;
    private static final int MASK_INT_00FF = 0x00FF;

    public static BufferedImage create3ByteBGRImage(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
    }

    public static BufferedImage to3ByteBGRImage(BufferedImage source) {
    	BufferedImage dest = create3ByteBGRImage(source.getWidth(), source.getHeight());
    	ImgUtils.copyImage(dest, source);
    	return dest;
    }

    public static int lsb4BytesCountFor(BufferedImage dest) {
        final int w = dest.getWidth(), h = dest.getHeight();
        final int bytesCount = w * h * 3 
                - 16;  // for encoding length, crc32
        final int maxIdx = bytesCount - bytesCount % 8;
        int res = maxIdx / 2;
        return res;
    }
    
    public static void putLsb4Bits(BufferedImage dest, byte[] encodeIn, int encodedLen) {
        DataBufferByte dataBuffer = (DataBufferByte) dest.getRaster().getDataBuffer();
        byte[] destData = dataBuffer.getData();
        final int w = dest.getWidth(), h = dest.getHeight();
        final int imgBytesCount = w * h * 3;
        final long crc32 = Crc32Utils.crc32(encodeIn, 0, encodedLen);
        if (DEBUG_HEADER) {
            System.out.println(" length to encode:" + encodedLen + " crc32:" + crc32 + " from " + w + "x" + h + "*rgb=" + imgBytesCount );
        }
        final int maxIdx = imgBytesCount - imgBytesCount % 8;
        int currBitsRemain = 0;
        int currBits = 0;
        int encodeIdx = 0;
        { // encode "encodedLen"
            int bitsForEncodedLen = encodedLen;
            for (int idx = 0; idx < 8; idx++) {
                int msb = (bitsForEncodedLen >> 28 ) & MASK_INT_LSB4;
                bitsForEncodedLen = bitsForEncodedLen << 4;
                byte prev = destData[idx];
                destData[idx] = withLsb4Bits(destData[idx], (byte)msb);
                if (DEBUG_HEADER) System.out.println(" msb4 len[" + idx + "] " + prev + " |& " + msb + "=" + destData[idx]);
            }
        }
        { // encode "crc32"
            int bitsForCrc32 = (int) crc32;
            for (int idx = 8; idx < 16; idx++) {
                int msb = (bitsForCrc32  >>> 28 ) & MASK_INT_LSB4;
                bitsForCrc32 = bitsForCrc32 << 4;
                byte prev = destData[idx];
                destData[idx] = withLsb4Bits(destData[idx], (byte)msb);
                if (DEBUG_HEADER) System.out.println(" msb4 crc32[" + idx + "] " + prev + " |& " + msb + "=" + destData[idx]);
            }
        }

        for(int idx = 16; idx < maxIdx; idx++) {
            if (currBitsRemain == 0) {
                if (encodeIdx > encodedLen) {
                    break;
                }
                currBits = encodeIn[encodeIdx] & MASK_INT_00FF;
                currBitsRemain = 8;

                if (DEBUG_SUMMARY) {
                    if (encodeIdx < 6 || encodeIdx >= encodedLen-6) {
                        System.out.println(" encoded[" + encodeIdx + "]:" + currBits);
                    }
                }

                ++encodeIdx;
            }
            int msb = (currBits & MASK_INT_00F0) >> 4;
            currBits = currBits << 4;
            currBitsRemain -= CST4;
            
            byte prev = destData[idx];
            destData[idx] = withLsb4Bits(destData[idx], (byte) msb);

            if (DEBUG_SUMMARY) {
                if (encodeIdx < 6 || encodeIdx >= encodedLen-6) {
                    System.out.println(" msb4[" + idx + "] " + prev + " |& " + msb + "=" + destData[idx]);
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

    public static int getLsb4Bits(byte[] decodeOut, BufferedImage img) {
        DataBufferByte dataBuffer = (DataBufferByte) img.getRaster().getDataBuffer();
        byte[] imgData = dataBuffer.getData();
        final int w = img.getWidth(), h = img.getHeight();
        final int bytesCount = w * h * 3;

        int resDecodedLen = 0;
        {
            // decode "encodeLen"
            for(int idx = 0; idx < 8; idx++) {
                int msb = imgData[idx] & MASK_BYTE_LSB4;
                resDecodedLen = (resDecodedLen << 4) | msb;
                if (DEBUG_HEADER) 
                    System.out.println(" msb4 len[" + idx + "] .. &| " + msb + " = " + imgData[idx]);
            }
        }
        
        final long resDecodedCrc32;
        {
            int crc32Bits = 0;
            // decode "encodeLen"
            for(int idx = 8; idx < 16; idx++) {
                int msb = imgData[idx] & MASK_INT_LSB4;
                crc32Bits = (crc32Bits << 4) | msb;
                if (DEBUG_HEADER) {
                    System.out.println(" msb4 crc32[" + idx + "] .. &| " + msb + " = " + imgData[idx]);
                }
            }
            resDecodedCrc32 = crc32Bits;
        }
        if (DEBUG_HEADER) {
        	System.out.println(" length to decode:" + resDecodedLen + " crc32:" + resDecodedCrc32 + " from " + w + "x" + h + "*rgb=" + bytesCount );
        }
        
        final int maxIdx = bytesCount - bytesCount % 8;
        int currBitsCount = 0;
        int currBits = 0;
        int decodeIdx = 0;
        for(int idx = 16; idx < maxIdx; idx++) {
            int msb = imgData[idx] & MASK_BYTE_LSB4;
            currBits = (currBits << 4) | msb;
            currBitsCount += CST4;
            if (DEBUG) {
                if (decodeIdx < 6 || decodeIdx >= resDecodedLen-6) {
                    System.out.println(" msb4[" + idx + "]: .. &| " + msb + " = " + imgData[idx]);
                }
            }

            
            if (currBitsCount == 8) {
                decodeOut[decodeIdx] = (byte) currBits;
                if (DEBUG) {
                    if (decodeIdx < 6 || decodeIdx >= resDecodedLen-6) {
                        System.out.println(" decoded[" + decodeIdx + "]:" + decodeOut[decodeIdx]);
                    }
                }
                        
                decodeIdx++;
                if (decodeIdx >= resDecodedLen) {
                    break;
                }
                currBitsCount = 0;
                currBits = 0;
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
    

    private static byte withLsb4Bits(byte value, byte lsb) {
        return (byte) ((value & MASK_BYTE_NOT_LSB4) | (MASK_BYTE_LSB4 & lsb));
    }
    
}
