package fr.an.img.stegano.split.impl;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import lombok.val;

public class CryptoUtilsTest {

    @Test
    public void testEncryptDecrypt() throws Exception {
        String password = "pass";
        val data = new byte[] { 1, 2, 3, 4, 5, 6 };
        
        val encryptedBuffer = new ByteArrayOutputStream();
        try(val encrypterOut = CryptoUtils.createEncryptOutputStreamFilter(encryptedBuffer, password)) {
            encrypterOut.write(data);
        }
        val encrypted = encryptedBuffer.toByteArray();
        
        val decryptedBuffer = new ByteArrayOutputStream();
        try(val decrypterOut = CryptoUtils.createDecryptOutputStreamFilter(decryptedBuffer, password)) {
            decrypterOut.write(encrypted);
        }
        val dataCheck = decryptedBuffer.toByteArray();
        Assert.assertEquals(data.length, dataCheck.length);
        Assert.assertArrayEquals(data, dataCheck);

        {
            val failDecryptedBuffer = new ByteArrayOutputStream();
            boolean fail = false;
            try(val failDecrypterOut = CryptoUtils.createDecryptOutputStreamFilter(failDecryptedBuffer, "bad-password")) {
                failDecrypterOut.write(encrypted);
            } catch(Exception ex) {
                fail = true; // ok! can not decrypt
            }
            if (!fail) {
                val failDataCheck = failDecryptedBuffer.toByteArray();
                Assert.assertFalse(Arrays.equals(failDataCheck, data));
            }
        }
    }

}
