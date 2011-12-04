package fr.an.eclipse.pattern.util;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import fr.an.eclipse.pattern.PatternUIPlugin;

/**
 * 
 */
public final class IOUtil {

    public static void safeClose(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception ex) {
                PatternUIPlugin.logWarning("failed to close inputstream! ... ignore", ex);
                // ignore, no rethrow!
            }
        }
    }

    public static void safeClose(Reader in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception ex) {
                PatternUIPlugin.logWarning("failed to close reader! ... ignore", ex);
                // ignore, no rethrow!
            }
        }
    }


    public static void safeClose(OutputStream p) {
        if (p != null) {
            try {
                p.close();
            } catch (Exception ex) {
                PatternUIPlugin.logWarning("failed to close outputstream! ... ignore", ex);
                // ignore, no rethrow!
            }
        }
    }

    public static void safeClose(Writer p) {
        if (p != null) {
            try {
                p.close();
            } catch (Exception ex) {
                PatternUIPlugin.logWarning("failed to close writer! ... ignore", ex);
                // ignore, no rethrow!
            }
        }
    }

    /**
     * utility to read a list of String: lines from an input stream
     */
    public static List<String> readAllLines(InputStream inStream, boolean ignoreCommentLines) throws IOException {
    	List<String> res = new ArrayList<String>();
    	readAllLines(inStream, res, ignoreCommentLines);
    	return res;
    }
    	
    /**
     * utility to read a list of String: lines from an input stream
     */
    public static void readAllLines(InputStream inStream, Collection<String> resList, boolean ignoreCommentLines) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
        for (;;) {
            String line = in.readLine();
            if (line == null) {
                break; // EOF
            }
            if (ignoreCommentLines) {
                if (line.startsWith("#") || line.trim().length() == 0) {
                    continue;
                }
                line = line.trim();
                if (line.length() > 0) {
                    resList.add(line);
                }
            } else {
                resList.add(line);
            }
        }
    }

    /**
     * @param file
     * @return
     */
    public static List<String> readAllLinesFromFile(File file, boolean ignoreCommentLines) {
        List<String>res = new ArrayList<String>();
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            readAllLines(in, res, ignoreCommentLines);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read file", ex);
        } finally {
            safeClose(in);
        }
        return res;
    }

    /**
     * @return
     */
    public static List<String> readAllLines(URL url, boolean ignoreCommentLines) {
    	List<String> res = new ArrayList<String>();
    	InputStream in = null;
    	try {
    		in = url.openStream();
    		readAllLines(in, ignoreCommentLines);
    	} catch (IOException ex) {
            throw new RuntimeException("Failed to read file", ex);
        } finally {
            safeClose(in);
        }
        return res;
    }
    
    /**
     * utility method to write file lines
     */
    public static void writeFileLines(File file, List<String> lines) {
        StringBuffer sb = new StringBuffer();
        for (String line : lines) {
            sb.append(line);
            sb.append("\n");
        }
        writeFileContent(file, sb.toString());
    }
    
    /**
     * utility method to write file lines
     */
    public static void writeFileContent(File file, String content) {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(content.getBytes());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write file", ex);
        } finally {
            safeClose(out);
        }
    }

   /**
    * utility method to write file lines
    */
   public static void writeFileContent(File file, byte[] content) {
       OutputStream out = null;
       try {
           out = new BufferedOutputStream(new FileOutputStream(file));
           out.write(content);
       } catch (IOException ex) {
           throw new RuntimeException("Failed to write file", ex);
       } finally {
           safeClose(out);
       }
   }
    
    /**
     * utility method to read file contents
     */
    public static String readFileContent(File file) {
    	return toString(file, Charset.defaultCharset());
    }
    
    /**
     * utility method to read file contents
     */
    public static String toString(InputStream inputStream, Charset encoding) {
    	StringBuilder res = new StringBuilder(4096);  
    	InputStreamReader in = null;
        try {
			in = new InputStreamReader(inputStream, encoding);
        	int bufferLen = 4096;
        	char[] buffer = new char[bufferLen];
            int readCount;
            while ((readCount = in.read(buffer)) != -1) {
            	res.append(buffer, 0, readCount);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read content", ex);
        } finally {
        	safeClose(in);
        }
        return res.toString();
    }

    /**
     * utility method to read file contents
     */
    public static String toString(File file, Charset encoding) {
    	InputStream fileInputStream = null;
    	try {
    		fileInputStream = new BufferedInputStream(new FileInputStream(file));	
    	} catch(IOException ex) {
    		throw new RuntimeException("Failed to open input file '" + file + "'", ex);
    	}
    	return toString(fileInputStream, encoding);
    }
    
    /**
     * utility method to read URL contents
     */
    public static String toString(URL url, Charset encoding) {
    	String res;
    	InputStream in = null;
    	try {
    		in = url.openStream();
    		res = toString(in, encoding);
    	} catch (IOException ex) {
            throw new RuntimeException("Failed to read url text content '" + url + "'", ex);
        } finally {
            safeClose(in);
        }
        return res;
    }
    

    public static byte[] toByteArray(InputStream input) {
    	try {
    		return IOUtils.toByteArray(input);	
    	} catch(IOException ex) {
    		throw new RuntimeException("Failed to read content", ex);
    	}
    }
    
    /**
     * utility method to read file contents
     */
    public static byte[] toByteArray(File file) {
    	long len = (long) file.length();
    	ByteArrayOutputStream bout = new ByteArrayOutputStream((int)len);
    	copyFileContent(file, bout);
    	return bout.toByteArray();
    }

    /**
     * utility method to read URL contents
     */
    public static byte[] toByteArray(URL url) {
    	byte[] res;
    	InputStream in = null;
    	try {
    		in = url.openStream();
    		res = IOUtils.toByteArray(in);
    	} catch (IOException ex) {
            throw new RuntimeException("Failed to read file", ex);
        } finally {
            safeClose(in);
        }
        return res;
    }
    
    /**
     * utility method to read file contents, and copy it into OutputStream parameter <code>dest</code>
     */
    public static void copyFileContent(File file, OutputStream dest) {
    	InputStream input = null;
    	try {
    		input = new BufferedInputStream(new FileInputStream(file));
    		IOUtils.copy(input, dest);
    	} catch(IOException ex) {
    		throw new RuntimeException("Failed to read file '" + file + "'", ex);
    	} finally {
    		safeClose(input); 
    	}
    }
    
    /**
     * utility method to read properties file, from ClassLoader resource name
     * @param cl
     * @param propertiesResourceName
     * @return
     */
	public static Properties readPropertiesFromResourceName(ClassLoader cl, String propertiesResourceName) {
		Properties res = null;
		URL propertiesURL = cl.getResource(propertiesResourceName);
        if (propertiesURL != null) {
        	res = new Properties();
            InputStream inputStream = null;
            try {
                inputStream = propertiesURL.openStream();
                res.load(inputStream);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                safeClose(inputStream);
            }
        }
		return res;
	}

	public static Map<String,String> parseMap(Reader reader) {
		Map<String,String> res = new HashMap<String,String>();
		try {
			BufferedReader lineReader = new BufferedReader(reader);
			String line = null;
			while(null != (line = lineReader.readLine())) {
				if (line.startsWith("#")) {
					continue;				
				}

				int indexEq = line.indexOf('=');
				if (indexEq == -1) {
					continue;
				}
				String left = line.substring(0, indexEq).trim();
				String right = line.substring(indexEq+1, line.length()).trim();

				res.put(left, right);
			}
		} catch(IOException ex) {
			// SHOULD NOT OCCUR
		}
		return res;
	}
	
}
