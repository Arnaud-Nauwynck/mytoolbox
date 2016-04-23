package fr.an.ruby2java.ruby2java;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.ast.Node;


/**
 * 
 */
public class App  {

	private File inputDir = new File("/home/csid/downloadTools/devops/puppet/lib");
	private File outputDir = new File("gen");
	
	private RubyInstanceConfig rubyConfig;
	private Ruby ruby;
	
	private R2JContextBuilder contextBuilder;
	
    public static void main(String[] args) {
    	try {
    		App app = new App();
    		app.parseArgs(args);
    		app.run();
    		
    		System.out.println("Finished");
    	} catch(Exception ex) {
    		System.err.println("Failed ... exiting");
    		ex.printStackTrace(System.err);
    	}
    }

	private void parseArgs(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	private void run() {
		this.rubyConfig = new RubyInstanceConfig();
		this.ruby = Ruby.newInstance(rubyConfig);
	
		this.contextBuilder = new R2JContextBuilder(ruby);
		
		recursiveScan("", inputDir, outputDir);
		
		String report = "Unsupported counts: " 
				+ contextBuilder.getUnsupportedCountMap().toString().replace(",", "\n");
		System.out.println(report);
		try {
			Path outputPath = Paths.get(outputDir.toURI());
			Files.write(outputPath.resolve("report.txt"), report.getBytes());
		} catch (IOException e) {
			System.out.println("Failed to write report");
		}
	}

	private void recursiveScan(String path, File input, File output) {
		if (input.isDirectory()) {
			File[] childList = input.listFiles();
			if (childList != null && childList.length > 0) {
				for(File childInputFile : childList) {
					String name = childInputFile.getName();
					if (name.startsWith(".")) {
						continue;
					}
					String childPath = path + "/" + name;
					if (childInputFile.isDirectory()) {
						File childOutputFile = new File(output, name);
						if (!childOutputFile.exists()) {
							childOutputFile.mkdirs();
						}
						
						recursiveScan(childPath, childInputFile, childOutputFile);
					} else {
						if (name.endsWith(".rb")) {
							File childOutputFile = new File(output, name.replaceAll(".rb", ".java"));
							scanFile(childPath, childInputFile, childOutputFile);
						} else {
							// ignore!
						}
					}
				}
			}
		} else {
			scanFile(path, input, output);
		}
	}

	private void scanFile(String path, File inputFile, File outputFile) {
		try {
			doScanFile(path, inputFile, outputFile);
		} catch(Exception ex) {
			System.out.println("Failed " + path + " ... ex:" + ex.getMessage());
		}
	}

	private void doScanFile(String path, File inputFile, File outputFile) {
		System.out.println("parsing " + path);
		Node ast;
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(inputFile));
			ast = ruby.parseFromMain(in, path);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to read file " + inputFile, ex);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception ex) {
				}
			}
		}
		// copy origin file
		Path inputFilePath = Paths.get(inputFile.toURI());
		Path outputCopyFilePath = Paths.get(new File(outputFile.getParentFile(), inputFile.getName()).toURI());
		if (! Files.exists(outputCopyFilePath)) {
			try {
				Files.copy(inputFilePath, outputCopyFilePath);
			} catch (IOException e) {
			}
		}
		
		R2JContext ctx = contextBuilder.forFile(path);
		PrintStream out = null;
		try {
			out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
			Ruby2JavaVisitor visitor = new Ruby2JavaVisitor(out, path, ctx, contextBuilder);
			
			ast.accept(visitor);
			
			out.flush();
		} catch(UnsupportedOperationException ex) {
			out.println("// UnsupportedOperationException: " + ex.getMessage());
		} catch(IOException ex) {
			throw new RuntimeException("Failed to write file " + outputFile, ex);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception ex) {
				}
			}
		}
	}
	
	
}
