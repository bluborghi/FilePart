package dev.blu;

import java.io.File;

public class FileHelper {
    public static String getParentDirectory(String dir) {
        return new File(dir).getAbsoluteFile().getParent();
    }

    public static String getFileExtension(String fileName) { //boh se no c'è string tokenizer
        if (hasExtension(fileName))
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else
            return "";
    }

    public static String removeFileExtension(String fileName) {
        if (hasExtension(fileName))
            return fileName.substring(0, fileName.lastIndexOf("."));
        else
            return fileName;
    }

    public static boolean hasExtension(String fileName) {
        return fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0;
    }
}
