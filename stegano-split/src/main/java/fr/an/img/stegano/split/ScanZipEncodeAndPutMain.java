package fr.an.img.stegano.split;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import fr.an.img.stegano.split.img.Img3ByteUtils;
import fr.an.img.stegano.split.img.ImgUtils;
import fr.an.img.stegano.split.img.ToImagesOutputStream;
import fr.an.img.stegano.split.img.ToImagesOutputStream.ImageDataFragment;
import fr.an.img.stegano.split.impl.CryptoUtils;
import fr.an.img.stegano.split.impl.IndexHtmlWriter;
import fr.an.img.stegano.split.impl.ZipOutputStreamFilesCopier;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class ScanZipEncodeAndPutMain {

    @Getter @Setter
    private File inputDir;
    @Getter @Setter
    private File inputFile;
    @Getter @Setter
    private Pattern inputFilePattern;

    
    @Getter @Setter
    private File inputImagesDir;
    @Getter @Setter
    private int inputImagesFirstIndex;

    @Getter @Setter
    private String password;

    @Getter @Setter
    private File outputDir;
    @Getter @Setter
    private String outputIndexHtmlFileName = "index.html";
    @Getter @Setter
    private String outputPageBaseHtmlFilename;
    @Getter @Setter
    private String outputImageName = "photo";
    @Getter @Setter
    private int outputImageScale = 10;
    @Getter @Setter
    private String extName = "bmp";
    
    @Getter @Setter
    private File outputFtpProps;
    
    
    @Getter @Setter
    boolean debug = false;
    
    public static void main(String[] args) {
        ScanZipEncodeAndPutMain app = new ScanZipEncodeAndPutMain();
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

    // ------------------------------------------------------------------------

   public void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.equals("-i")) {
                inputFile = new File(args[++i]);
            } else if (a.equals("--pattern")) {
                inputFilePattern = Pattern.compile(args[++i]);
            } else if (a.equals("--inputDir")) {
                inputDir = new File(args[++i]);
            } else if (a.equals("--inputImagesDir")) {
                inputImagesDir = new File(args[++i]);
            } else if (a.equals("--inputImagesFirstIndex")) {
                inputImagesFirstIndex = Integer.parseInt(args[++i]);
            } else if (a.equals("--outputImageName")) {
                outputImageName = args[++i];
            } else if (a.equals("--outputIndexHtmlFileName")) {
                outputIndexHtmlFileName = args[++i]; 
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
        if (outputImageName == null) {
            outputImageName = "photo";
        }
        if (outputPageBaseHtmlFilename == null) {
            outputPageBaseHtmlFilename = "index-" + outputImageName; 
        }
    }

    public void run() throws Exception {
        if (outputDir == null) {
            try {
                outputDir = File.createTempFile( "stegano", "");
            } catch ( IOException e ) {
                throw new RuntimeException();
            }
            outputDir.delete();
            outputDir.mkdir();
        }
        if (! outputDir.exists()) {
            outputDir.mkdirs();
        }

        System.out.println("Scanning inputDir=" + inputDir + " => zip+split+encode into outputDir=" + outputDir);

        List<File> inputImageFiles = new ArrayList<>();
        if (inputImagesDir != null && inputImagesDir.exists() && inputImagesDir.isDirectory()) {
            inputImageFiles.addAll(Arrays.asList(inputImagesDir.listFiles()));
            if (inputImagesFirstIndex < 0) {
                inputImagesFirstIndex = new Random().nextInt(inputImageFiles.size()-1);
            }
        }
        Supplier<BufferedImage> rawImagesSupplier = new Supplier<BufferedImage>() {
            int roundRobin = inputImagesFirstIndex;

            @Override
            public BufferedImage get() {
                if (!inputImageFiles.isEmpty()) {
                    for (int tryCount = 0; tryCount < 5; tryCount++) {
                        try {
                            BufferedImage res = tryGetImage(inputImageFiles.get(roundRobin++));
                            if (res != null) {
                                return res;
                            }
                        } catch(Exception ex) {
                            // ignore .. try more
                            continue;
                        }
                    }
                }
                return createImage();
            }
            
            public BufferedImage createImage() {
                BufferedImage img = Img3ByteUtils.create3ByteBGRImage(3000, 3000);
                ImgUtils.dummyDrawStringInImage(img);
                return img;
            }
            
            public BufferedImage tryGetImage(File imgFile) {
                BufferedImage rawImg;
                try {
                    rawImg = ImgUtils.readImage(imgFile);
                } catch(Exception ex) {
                    System.err.println("Failed to load img '" + imgFile + "' ..ignore " + ex.getMessage());
                    return null;
                }
                // convert to BufferedImage.TYPE_3BYTE_BGR
                try {
                    BufferedImage img3Bgr = Img3ByteUtils.to3ByteBGRImage(rawImg);
                    return img3Bgr;
                } catch(Exception ex) {
                    System.err.println("Failed to convert img to 3bytes BGR '" + imgFile + "' ..ignore " + ex.getMessage());
                }
                return null;
            }
        };

        IndexHtmlWriter indexHtmlWriter = new IndexHtmlWriter(outputDir, outputIndexHtmlFileName, outputPageBaseHtmlFilename);

        Consumer<ImageDataFragment> imagesConsumer = (frag) -> {
            String imgFileName = outputImageName + "-" + frag.imageIndex + "." + extName;
            int w = frag.img.getWidth(), h = frag.img.getHeight();
            indexHtmlWriter.addImgFile(imgFileName, w/outputImageScale, h/outputImageScale);
            
            System.out.println("encode save fragment '" + imgFileName + "' len:" + frag.encodedLen + " crc:" + frag.crc32);
            // save image (for debug only?)
            ImgUtils.saveImage(frag.img, extName, new File(outputDir, imgFileName));
            
            // upload file..
        };
        
        Random rand = new Random(password.hashCode());
        
        try(val toImagesOutputStream = 
                new ToImagesOutputStream(rawImagesSupplier, rand, imagesConsumer)
                ) {
        
            OutputStream encryptOutputStream = 
                CryptoUtils.createEncryptOutputStreamFilter(toImagesOutputStream, password);
        
            val zipOutputStreamFileCopier = new ZipOutputStreamFilesCopier(
                    new BufferedOutputStream(encryptOutputStream, 1024*1024));
            
            try {
                
                if (inputFile != null) {
                    String relativePathName = inputFile.getName();
                    zipOutputStreamFileCopier.putNextEntry(relativePathName, inputFile);
                } else {
                    zipOutputStreamFileCopier.scanAndPutEntries( inputDir, inputFilePattern );
                }
                
                zipOutputStreamFileCopier.close();
                
                indexHtmlWriter.close();
                
                // TODO ... send to ftp 
                
                
            } catch(Exception ex) {
                throw new RuntimeException("Failed", ex);
            }
        }
    }

    
}
