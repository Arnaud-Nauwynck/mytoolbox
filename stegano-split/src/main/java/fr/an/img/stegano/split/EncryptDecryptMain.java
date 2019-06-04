package fr.an.img.stegano.split;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import fr.an.img.stegano.split.impl.CryptoUtils;
import fr.an.img.stegano.split.impl.IOUtils;
import lombok.Getter;
import lombok.Setter;

public class EncryptDecryptMain {

    @Getter @Setter
    private File inputFile;
    
    @Getter @Setter
    private File outputFile;
    
    @Getter @Setter
    private String password;

    @Getter @Setter
    private boolean encryptOrDecrypt;

    public static void main(String[] args) {
        EncryptDecryptMain app = new EncryptDecryptMain();
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
            } else if (a.equals("-o")) {
                outputFile = new File(args[++i]);
            } else if (a.equals("--password")) {
                password = args[++i];
            } else if (a.equals("--encrypt")) {
                encryptOrDecrypt = true;
            } else if (a.equals("--decrypt")) {
                encryptOrDecrypt = false;
            }
        }
    }
        
    public void run() throws Exception {
        System.out.println(((encryptOrDecrypt)? "Encrypt" : "Decrypt") + " file '" + inputFile + "' -> '" + outputFile + "'");
        try (OutputStream outputStream =  CryptoUtils.createEncryptOrDecryptOutputStreamFilter(encryptOrDecrypt, 
                        new BufferedOutputStream(new FileOutputStream(outputFile)), 
                        password)) {
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile))) {
                IOUtils.copy(inputStream, outputStream);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
    }

}
