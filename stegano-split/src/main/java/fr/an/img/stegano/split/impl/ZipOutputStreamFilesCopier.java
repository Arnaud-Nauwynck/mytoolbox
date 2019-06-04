package fr.an.img.stegano.split.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipOutputStreamFilesCopier {

    private ZipOutputStream zipOut;
        
    public ZipOutputStreamFilesCopier(OutputStream out) {
        this.zipOut = new ZipOutputStream (out);
    }

    public void close() throws IOException {
        zipOut.finish();
        zipOut.close();
    }
    
    public void putNextEntry(String relativePathName, File file) throws IOException {
        ZipEntry outZe = new ZipEntry(relativePathName);
        outZe.setTime(file.lastModified());
        // outZe.setSize(file.length());
        zipOut.putNextEntry(outZe);
        try (FileInputStream fIn = new FileInputStream(file)) {
            IOUtils.copy(fIn, zipOut);
        } catch(IOException ex) {
            throw new RuntimeException("Failed", ex);
        }
        zipOut.closeEntry();
    }
    
    public void scanAndPutEntries(
                                  File inputDir,
                                  Pattern inputFilePattern
                    ) throws IOException {
        Path inputDirPath = Paths.get(inputDir.toURI());
        Files.walkFileTree(inputDirPath, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile()) {
                    File file = filePath.toFile();
                    String relativePathName = inputDirPath.relativize(filePath).toString();
                    // System.out.print('.');

                    putNextEntry(relativePathName, file);
                    
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
