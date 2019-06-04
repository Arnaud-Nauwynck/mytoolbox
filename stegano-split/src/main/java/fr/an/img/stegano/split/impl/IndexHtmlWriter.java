package fr.an.img.stegano.split.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class IndexHtmlWriter {
    
    File outputDir;
    String outputIndexHtmlFile;
    String outputPageBaseHtmlFilename;
    int imgPageCount = 0;
    int maxImgPerPage = 10;
    int pageIndex = 0;
    PrintStream pageHtmlOut;
    
    PrintStream indexHtmlOut;
    
    @Getter
    List<File> resultHtmlFiles = new ArrayList<>();

    public IndexHtmlWriter(File outputDir, String outputIndexHtmlFile, String outputPageBaseHtmlFilename) {
        this.outputDir = outputDir;
        this.outputIndexHtmlFile = outputIndexHtmlFile;
        this.outputPageBaseHtmlFilename = outputPageBaseHtmlFilename;
        indexHtmlOut = newFilePrintStream(outputIndexHtmlFile);
        indexHtmlOut.print("<html>\n<body>\n");
        
        openPageHtml();
    }

    private void openPageHtml() {
        String pageName = outputPageBaseHtmlFilename + "-" + pageIndex + ".html";
        pageHtmlOut = newFilePrintStream(pageName);
        pageHtmlOut.print("<html>\n<body>\n");
        
        indexHtmlOut.println("<A href='" + pageName + "'>page</A>\n");
    }

    protected PrintStream newFilePrintStream(String fileName) {
        try {
            File file = new File(outputDir, fileName);
            resultHtmlFiles.add(file);
            return new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void addImgFile(String imgFileName, int w, int h) {
        imgPageCount++;
        pageHtmlOut.print("<img src='" + imgFileName + "' width='" + w + "' height='" + h + "'/>\n");
        if (imgPageCount > maxImgPerPage) {
            closePageHtml();
            imgPageCount = 0;
            pageIndex++;
            openPageHtml();
        }
    }
    
    public void close() {
        indexHtmlOut.print("</body>\n</html>\n");
        indexHtmlOut.close();
        indexHtmlOut = null;
        
        closePageHtml();
    }

    private void closePageHtml() {
        pageHtmlOut.print("</body>\n</html>\n");
        pageHtmlOut.close();
        pageHtmlOut = null;
    }
    
}