package fr.an.img.stegano.split;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SteganoEncodeMainTest {

    @Test
    public void testRun_test1() throws Exception {
        // Prepare
        // dir generated by corresponding SteganoEncodeMain test (copyed from "target/tests/test1" )
        File inputDir = new File("src");
        File outputDir = new File("target/tests/test1");
        if (outputDir.exists()) {
            FileUtils.deleteDirectory(outputDir);
        }
        outputDir.mkdirs();
        SteganoEncodeMain sut = new SteganoEncodeMain();
        sut.setInputDir(inputDir);
        // sut.setExtName(".png");
        sut.setMaxPartLen(10000);
        sut.setOutputDir(outputDir);
        sut.setOutputFilename("test1");
        // Perform
        sut.run();
        // Post-check
        Assert.assertTrue(6 <= outputDir.listFiles().length);
        Assert.assertTrue(new File(outputDir, "test1.html").exists());
        Assert.assertTrue(new File(outputDir, "test1-0.html").exists());
        Assert.assertTrue(new File(outputDir, "test1-0.png").exists());
        Assert.assertTrue(new File(outputDir, "test1-1.png").exists());
        Assert.assertTrue(new File(outputDir, "test1-2.png").exists());
        Assert.assertTrue(new File(outputDir, "test1-3.png").exists());
    }

    
    @Test @Ignore
    public void testRun_test_huge() throws Exception {
        // Prepare
        String userDir = System.getProperty("user.home");
        File inputDir = new File(userDir + "/.m2/repository");
        File outputDir = new File("target/tests/test_huge");
        if (outputDir.exists()) {
            FileUtils.deleteDirectory(outputDir);
        }
        outputDir.mkdirs();
        SteganoEncodeMain sut = new SteganoEncodeMain();
        sut.setInputDir(inputDir);
        // sut.setMaxPartLen(2*1024*1024);
        sut.setOutputDir(outputDir);
        sut.setOutputFilename("test1");
        sut.setOutputZipPath("plugins/");
        // Perform
        sut.run();
        // Post-check
        Assert.assertTrue(80 < outputDir.listFiles().length);
        Assert.assertTrue(new File(outputDir, "test1.html").exists());
        Assert.assertTrue(new File(outputDir, "test1-0.html").exists());
        Assert.assertTrue(new File(outputDir, "test1-1.html").exists());
        Assert.assertTrue(new File(outputDir, "test1-80.png").exists());
    }

    
}