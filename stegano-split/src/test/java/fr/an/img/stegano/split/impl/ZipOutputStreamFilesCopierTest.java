package fr.an.img.stegano.split.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.Test;

public class ZipOutputStreamFilesCopierTest {

    @Test
    public void testZip() throws Exception {
        File srcZip = new File("target/src.zip");
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(srcZip))) {
            ZipOutputStreamFilesCopier copier = new ZipOutputStreamFilesCopier(out);
            copier.scanAndPutEntries(new File("src"), null);
        }
        ZipUtils.unzipToDir(new File("target/src2"), srcZip);
    }

}
