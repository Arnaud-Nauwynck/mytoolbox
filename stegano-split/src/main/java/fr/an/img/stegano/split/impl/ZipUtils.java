package fr.an.img.stegano.split.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    public static void unzipToDir(File outputDir, File zipFile) {
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)))) {
            unzipToDir(zis, outputDir);
        } catch(IOException ex) {
            throw new RuntimeException("Failed to unzip file:" + zipFile +"' to dir:'" + outputDir + "'", ex);
        }
    }

    protected static void unzipToDir(ZipInputStream zis, File outputDir) throws IOException {
        ZipEntry ze;
        while((ze = zis.getNextEntry()) != null) {
            String fileName = ze.getName();
            File ouputFile = new File(outputDir, fileName);
            if (ze.isDirectory()) {
                ouputFile.mkdirs();
            } else {
                File parentDir = ouputFile.getParentFile();
                parentDir.mkdirs();
                try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(ouputFile))) {             
                    IOUtils.copy(zis, fos);
                }
            }
            zis.closeEntry();
        }
    }

}
