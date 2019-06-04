package fr.an.img.stegano.split;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import fr.an.img.stegano.split.img.Img3ByteUtils;
import fr.an.img.stegano.split.img.ImgUtils;
import fr.an.img.stegano.split.impl.Crc32Utils;
import fr.an.img.stegano.split.impl.CryptoUtils;
import fr.an.img.stegano.split.impl.ZipUtils;
import lombok.Getter;
import lombok.Setter;

public class GetDecodeAndUnzipMain {

    @Getter @Setter
    private File inputDir;
    @Getter @Setter
    private String inputFileBaseName = "photo";
    @Getter @Setter
    private String inputFileExt = "bmp";
    
    @Getter @Setter
    private String password;
    
    @Getter @Setter
    private File outputDir;
    
    @Getter @Setter
    private boolean decodeZipOnly;
    
    public static void main(String[] args) {
        GetDecodeAndUnzipMain app = new GetDecodeAndUnzipMain();
        app.parseArgs(args);
        try {
            app.run();
            System.out.println("Finished, exit(0)");
        } catch(Exception ex) {
            System.err.println("Failed .. exit(-1)");
            ex.printStackTrace(System.err);
            System.exit(-1);
        }
    }
    
    public void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.equals("--inputDir")) {
                inputDir = new File(args[++i]);
            } else if (a.equals("--inputFileBaseName")) {
                inputFileBaseName = args[++i];
            } else if (a.equals("--inputFileExt")) {
                inputFileExt = args[++i];
            } else if (a.equals("--outputDir")) {
                outputDir = new File(args[++i]);
            } else if (a.equals("--password")) {
                password = args[++i];
            }
        }
        if (password == null) {
            password = "pass!123"; // !!
        }
        if (inputDir == null) {
            inputDir = new File(".");
        }
        if (inputFileBaseName == null) {
            inputFileBaseName = "image";
        }
        if (outputDir == null) {
            outputDir = inputDir;
        }
    }
    
    public void run() throws Exception {
        System.out.println("Scanning to restore from inputDir=" + inputDir + " => decode+unzip into outputDir=" + outputDir);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        byte[] decodeBuffer = new byte[10*1024*1024];
        
        File zipFile = File.createTempFile(inputFileBaseName, "-restore.zip");
        try (OutputStream decryptOutput = 
                CryptoUtils.createDecryptOutputStreamFilter(
                        new BufferedOutputStream(new FileOutputStream(zipFile))
                , password)
                ) {
            
            for (int i = 1; ; i++) {
                File imgFile = new File(inputDir, inputFileBaseName + "-" + i + "." + inputFileExt);
                if (! imgFile.exists()) {
                    System.out.println("finished scanning file ( " + (i-1) + ")");
                    break;
                }
    
                try {
                    // read(decode) img file
                    BufferedImage img = ImgUtils.readImage(imgFile);
        
                    int maxDecodedLen = Img3ByteUtils.lsb4BytesCountFor(img);
                    // extract byte[] content from image
                    if (maxDecodedLen >= decodeBuffer.length) {
                        decodeBuffer = new byte[maxDecodedLen];
                    }
                    int decodedLen = Img3ByteUtils.getLsb4Bits(decodeBuffer, img);
                    long decodeCrc32 = Crc32Utils.crc32(decodeBuffer, 0, decodedLen);
                    System.out.println("decode load fragment " + imgFile + " len:" + decodedLen + " crc:" + decodeCrc32);
                    
                    decryptOutput.write(decodeBuffer, 0, decodedLen);
                    
                } catch(Exception ex) {
                    throw new RuntimeException("Failed", ex);
                }
            }
        }

        System.out.println("Unzipping to dir:" + outputDir);
        try {
        	ZipUtils.unzipToDir(outputDir, zipFile);

        	zipFile.delete();
        } catch(Exception ex) {
        	String msg = "Failed to restore unzip from file '" + zipFile + "' " + ex.getMessage();
			System.err.println(msg);
        	throw new RuntimeException(msg, ex);
        }
    }


}
