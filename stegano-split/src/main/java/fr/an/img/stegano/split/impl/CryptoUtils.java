package fr.an.img.stegano.split.impl;

import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.val;

public class CryptoUtils {

    public static CipherOutputStream createEncryptOrDecryptOutputStreamFilter(
            boolean encryptOrDecrypt,
            OutputStream output, String password) {
        return createCipherOutputStreamFilter(output, password, (encryptOrDecrypt)? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE);
    }
    public static CipherOutputStream createEncryptOutputStreamFilter(
            OutputStream output, String password) {
        return createCipherOutputStreamFilter(output, password, Cipher.ENCRYPT_MODE);
    }

    public static CipherOutputStream createDecryptOutputStreamFilter(
            OutputStream output, String password) {
        return createCipherOutputStreamFilter(output, password, Cipher.DECRYPT_MODE);
    }

                    
    public static CipherOutputStream createCipherOutputStreamFilter(
            final OutputStream output,
            String password,
            int opMode
            ) {
        try {
            byte[] keyBytes = ((password != null)? password : "pass").getBytes();
            keyBytes = Arrays.copyOf(sha256(keyBytes), 16);
            
            Random initPseudoRandom = new Random(0);
            byte[] initVector = new byte[16];
            for (int i = 0; i < 16; i++) {
                initVector[i] = (byte) initPseudoRandom.nextInt();
            }
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(opMode, skeySpec, iv);
            
            val encryptOutputStream = new CipherOutputStream(output, cipher);
            return encryptOutputStream;
        } catch(Exception ex) {
            throw new RuntimeException("Failed to create cipher", ex);
        }
    }
    
    public static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Failed", ex);
        }
    }

}
