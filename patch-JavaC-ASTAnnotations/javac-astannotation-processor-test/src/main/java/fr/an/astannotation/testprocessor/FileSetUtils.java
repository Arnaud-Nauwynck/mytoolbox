package fr.an.astannotation.testprocessor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileSetUtils {
    private static final String FILE_DELIMITER = ",";

    /**
     * This method accepts the comma-separated file names, splits it using the
     * defined delimiter. A list of valid file objects will be created and
     * returned to main method.
     * 
     * @param fileNames
     *            Comma-separated file names
     * @return List of valid source file objects
     */
    public static List<File> getFilesAsList(String fileNames) {
        List<File> files = new LinkedList<File>();
        // split the filenames using the delimiter
        String[] filesArr = fileNames.split(FILE_DELIMITER);
        File sourceFile = null;
        for (String fileName : filesArr) {
            sourceFile = new File(fileName);
            if (sourceFile != null && sourceFile.exists()) {
                files.add(sourceFile);
            } else {
                System.out.println(fileName + " is not a valid file. "
                        + "Ignoring the file ");
            }
        }
        return files;
    }

}
