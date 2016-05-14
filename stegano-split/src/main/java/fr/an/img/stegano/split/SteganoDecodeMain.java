package fr.an.img.stegano.split;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

public class SteganoDecodeMain {

    private File inputDir;
    private String inputFileBaseName;
    private String inputFileExt = ".png";
    private File outputDir;
    private boolean decodeZipOnly;
    private String outputFilename;
    
    public static void main(String[] args) {
        SteganoDecodeMain app = new SteganoDecodeMain();
        app.parseArgs(args);
        app.run();
    }
    
    public void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.equals("-i")) {
                inputFileBaseName = args[++i];
            } else if (a.equals("--inputFileExt")) {
                inputFileExt = args[++i];
            } else if (a.equals("-d")) {
                inputDir = new File(args[++i]);
            } else if (a.equals("-o")) {
                outputFilename = args[++i];
            } else if (a.equals("--outputDir")) {
                outputDir = new File(args[++i]);
            }
        }
        if (inputDir == null) {
            inputDir = new File(".");
        }
        if (inputFileBaseName == null) {
            inputFileBaseName = "img";
        }
        if (outputDir == null) {
            outputDir = inputDir;
        }
        if (outputFilename == null) {
            outputFilename = "out";
        }
    }
    
    public void run() {
        for (int i = 0; ; i++) {
            File imgFile = new File(inputDir, inputFileBaseName + "-" + i + inputFileExt);
            if (! imgFile.exists()) {
                break;
            }

            try {
                // read(decode) img file
                BufferedImage img;
                try {
                    img = ImageIO.read(imgFile);
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to read file " + imgFile, ex);
                }
    
                // copy byte[] content from png to dest zip
                DataBufferByte imgDataBuffer = (DataBufferByte) img.getRaster().getDataBuffer();
                byte[] imgData = imgDataBuffer.getData();
                DataInputStream din = new DataInputStream(new ByteArrayInputStream(imgData));
                int fileLen = din.readInt();
    
                if (decodeZipOnly) {
                    File zipFile = new File(outputDir, outputFilename + "-" + i + ".zip");
                    try (OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(zipFile))) {
                        fileOut.write(imgData, 4, fileLen);
                    }
                } else {
                    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(imgData, 4, fileLen))) {
                        unzipToDir(zis, outputDir);
                    }
                }
                
            } catch(Exception ex) {
                throw new RuntimeException("Failed", ex);
            }
        }
    }

    public static void unzipToDir(ZipInputStream zis, File outputDir) throws IOException {
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

    public File getInputDir() {
        return inputDir;
    }

    public void setInputDir(File inputDir) {
        this.inputDir = inputDir;
    }

    public String getInputFileBaseName() {
        return inputFileBaseName;
    }

    public void setInputFileBaseName(String inputFileBaseName) {
        this.inputFileBaseName = inputFileBaseName;
    }

    public String getInputFileExt() {
        return inputFileExt;
    }

    public void setInputFileExt(String inputFileExt) {
        this.inputFileExt = inputFileExt;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public boolean isDecodeZipOnly() {
        return decodeZipOnly;
    }

    public void setDecodeZipOnly(boolean decodeZipOnly) {
        this.decodeZipOnly = decodeZipOnly;
    }
    
    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String p) {
        this.outputFilename = p;
    }

}
