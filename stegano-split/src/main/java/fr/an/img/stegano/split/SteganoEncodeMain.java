package fr.an.img.stegano.split;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class SteganoEncodeMain {

    private File inputDir;
    private Pattern inputFilePattern;

    private int maxPartLen = 2*1024*1024; // Mo
    private String outputZipPath = "";
    private File outputDir;
    private String outputFilename;
    private String extName = ".png";
    
    
    public static void main(String[] args) {
        SteganoEncodeMain app = new SteganoEncodeMain();
        app.parseArgs(args);
        app.run();
    }

    // ------------------------------------------------------------------------
    
    
    public void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.equals("-i")) {
                inputFilePattern = Pattern.compile(args[++i]);
            } else if (a.equals("-d")) {
                inputDir = new File(args[++i]);
            } else if (a.equals("-o")) {
                outputFilename = args[++i];
            } else if (a.equals("--outputZipPath")) {
                outputZipPath = args[++i];
            } else if (a.equals("--outputDir")) {
                outputDir = new File(args[++i]);
            }
        }
        if (inputDir == null) {
            inputDir = new File(".");
        }
        if (outputDir == null) {
            outputDir = inputDir;
        }
        if (outputFilename == null) {
            outputFilename = "img";
        }
    }
    
    public File getInputDir() {
        return inputDir;
    }

    public void setInputDir(File inputDir) {
        this.inputDir = inputDir;
    }

    public Pattern getInputFilePattern() {
        return inputFilePattern;
    }

    public void setInputFilePattern(Pattern inputFilePattern) {
        this.inputFilePattern = inputFilePattern;
    }

    public int getMaxPartLen() {
        return maxPartLen;
    }

    public void setMaxPartLen(int maxPartLen) {
        this.maxPartLen = maxPartLen;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public String getExtName() {
        return extName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }
    
    public String getOutputZipPath() {
        return outputZipPath;
    }

    public void setOutputZipPath(String outputZipPath) {
        this.outputZipPath = outputZipPath;
    }

    public void run() {
        if (! outputDir.exists()) {
            outputDir.mkdirs();
        }
        IndexedFilesZipper indexedFilesZipper = new IndexedFilesZipper(maxPartLen);
        
        try {
            IndexHtmlWriter indexHtmlWriter = new IndexHtmlWriter(outputDir, outputFilename);
            
            Path inputDirPath = Paths.get(inputDir.toURI());
            Files.walkFileTree(inputDirPath, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isRegularFile()) {
                        String fileName = filePath.getFileName().toString();
                        File file = filePath.toFile();
                        String relativePathName = outputZipPath + inputDirPath.relativize(filePath).toString();
                        if (inputFilePattern != null && ! inputFilePattern.matcher(fileName).matches()) {
                            System.out.println("skip " + relativePathName);
                            return FileVisitResult.CONTINUE;
                        }
                        // System.out.println("processing " + relativePathName);
                        System.out.print('.');

                        indexedFilesZipper.putNextEntry(relativePathName, file, 
                            (buffer,zi) -> {
                                String imgFileName = outputFilename + "-" + zi + extName;
                                indexHtmlWriter.addImgFile(imgFileName);
                                File outputImgFile = new File(outputDir, imgFileName);
                                writeWrapPNG(buffer, outputImgFile);
                            });
                        
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            
            indexHtmlWriter.close();
        } catch(Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
    }
    
    private void writeWrapPNG(ByteBuffer buffer, File outputImgFile) {
        int fileLen = buffer.remaining();

        // copy buffer to img
        int headerLen = 4;
        int fileLenPad4 = fileLen + 4 - fileLen % 4 + headerLen;
        int pixelCount = fileLenPad4 / 4; 
        int imgW = (int) Math.min(1+Math.sqrt(pixelCount), 1024);
        int imgH = (int) (pixelCount + imgW-1) / imgW;
        int checkImgByteLen = imgW * imgH * 4;
        if (checkImgByteLen < fileLenPad4) {
            imgH++;
            checkImgByteLen = imgW * imgH * 4;
        }
        if (checkImgByteLen < fileLen) {
            throw new IllegalStateException();
        }
        BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_4BYTE_ABGR);
        DataBufferByte imgDataBuffer = (DataBufferByte) img.getRaster().getDataBuffer();
        byte[] imgData = imgDataBuffer.getData();
        if (imgData.length != checkImgByteLen) {
            throw new IllegalStateException();
        }
        
        // cf DataOutputStream.writeInt
        imgData[0] = (byte) ((fileLen >>> 24) & 0xFF);
        imgData[1] = (byte) ((fileLen >>> 16) & 0xFF);
        imgData[2] = (byte) ((fileLen >>>  8) & 0xFF);
        imgData[3] = (byte) ((fileLen >>>  0) & 0xFF);
        
        buffer.get(imgData, 4, fileLen);

        boolean debug = false;
        if (debug) {
            File checkZipFile = new File(outputImgFile.getParentFile(), outputImgFile.getName() + ".zip");
            try (OutputStream checkZipOut = new BufferedOutputStream(new FileOutputStream(checkZipFile))) {
                checkZipOut.write(imgData, 4, fileLen);
            } catch(Exception ex) {
                throw new RuntimeException("Failed to write file " + checkZipFile, ex);
            }
        }
        
        // write(encode) img file
        try {
            ImageIO.write(img, "PNG", outputImgFile);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write file " + outputImgFile, ex);
        }
        long resultLen = outputImgFile.length();
        System.out.println("flush writing " + outputImgFile + " len:" + (fileLen/1024) + " ko"
            + " => png: " + (resultLen/1024) + " ko, overhead: " + (resultLen-fileLen));
    }
    
}
