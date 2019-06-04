package fr.an.img.stegano.split.impl;

import java.util.zip.CRC32;

public class Crc32Utils {

    /** long for unsigned ... but 4 bits= 32 =unsigned int */
    public static long crc32(byte[] data, int off, int len) {
        CRC32 crc = new CRC32();
        crc.update(data, off, len);
        return crc.getValue();
    }
}
