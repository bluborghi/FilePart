package dev.blu.model.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;

import dev.blu.model.core.FileMerger;
import dev.blu.model.core.FileSplitterByMaxSize;

/**
 * Utility class used by {@link FileSplitterByMaxSize} and {@link FileMerger}
 * @author blubo
 *
 */
public class FileHelper {
    private final static int DEFAULT_BUFFER_LENGTH = 1024 * 1024 * 1; //1MiB

    /**
     * Gets the parent directory of a file directory
     * @param path The path of the input file
     * @return The path of the parent directory
     */
    public static String getParentDirectory(String path) {
        return new File(path).getAbsoluteFile().getParent();
    }

    /**
     * Gets the file extension
     * @param fileName the file name or path
     * @return The extension of the name or path
     */
    public static String getFileExtension(String fileName) { 
        if (hasExtension(fileName))
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else
            return "";
    }

    /**
     * Remove file extension
     * @param fileName The file name or path
     * @return The file name or path without extension
     */
    public static String removeFileExtension(String fileName) {
        if (hasExtension(fileName))
            return fileName.substring(0, fileName.lastIndexOf("."));
        else
            return fileName;
    }

    /**
     * Checks if the file or path has an extension
     * @param fileName The file or path
     * @return <code>true</code> if the file or path has an extension, <code>false</code> otherwise
     */
    public static boolean hasExtension(String fileName) {
        return fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0;
    }

    /**
     * transfers data from an {@link InputStream} to an {@link OutputStream}
     * @param fis The input stream
     * @param fos The output stream
     * @param partLength the length (in bytes) to transfer
     * @param bytesTransfered the bytes actually transfered (needed to calculate the percentage)
     * @param stop a stop flag that can be turned on from outside
     * @throws IOException I/O error
     */
    public static void transfer(FileInputStream fis, FileOutputStream fos, long partLength, long[] bytesTransfered,  boolean[] stop) throws IOException {
        transfer(fis,fos,partLength, DEFAULT_BUFFER_LENGTH, bytesTransfered, stop);
    }

    private static void transfer(FileInputStream fis, FileOutputStream fos, long partLength, int maxBufferLength, long[] bytesTransferd, boolean[] stop) throws IOException {
        int buffer_length;
        int last_buffer_length;
        byte[] buffer;
        long numberOfTransfers;

        if (partLength > maxBufferLength) {
            buffer_length = maxBufferLength;
            numberOfTransfers = (int) ((partLength - 1) / buffer_length) + 1;
            last_buffer_length = (int) (partLength - buffer_length * (numberOfTransfers - 1));
            buffer = new byte[buffer_length];
            for (long i = numberOfTransfers; i > 1 && !stop[0]; i--) { //only if there's more than one part (you need to transfer more bytes than the buffer size)
            	fis.read(buffer);
                fos.write(buffer);
                bytesTransferd[0]+=buffer.length;
            }
        } else {
            last_buffer_length = (int) partLength; //cast to int is ok (partLength <= MAX_BUF_LENGTH)
        }
        if (stop[0])
        	return;
        
        buffer = new byte[last_buffer_length];
        fis.read(buffer);
        fos.write(buffer);
        bytesTransferd[0]+=buffer.length;
    }
}
