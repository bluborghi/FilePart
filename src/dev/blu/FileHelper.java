package dev.blu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class FileHelper {
    private final static int DEFAULT_BUFFER_LENGTH = 1024 * 1024 * 1; //1MiB

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

    public static void transfer(FileInputStream fis, FileOutputStream fos, long partLength) throws IOException {
        transfer(fis,fos,partLength, DEFAULT_BUFFER_LENGTH);
    }

    public static void transfer(FileInputStream fis, FileOutputStream fos, long partLength, int maxBufferLength) throws IOException {
        int buffer_length;
        int last_buffer_length;
        byte[] buffer;
        long numberOfTransfers;

        if (partLength > maxBufferLength) {
            buffer_length = maxBufferLength;
            numberOfTransfers = (int) ((partLength - 1) / buffer_length) + 1;
            last_buffer_length = (int) (partLength - buffer_length * (numberOfTransfers - 1));
            buffer = new byte[buffer_length];
            for (long i = numberOfTransfers; i > 1; i--) { //only if there's more than one part (you need to transfer more bytes than the buffer size)
                fis.read(buffer);
                fos.write(buffer);
            }
        } else {
            last_buffer_length = (int) partLength; //cast to int is ok (partLength <= MAX_BUF_LENGTH)
        }

        buffer = new byte[last_buffer_length];
        fis.read(buffer);
        fos.write(buffer);
    }

    public static CRC32 getFileCRC32(File f, int maxBufferLength) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        CRC32 crc = new CRC32();
        byte[] buffer;

        while (fis.available()>=maxBufferLength){
            buffer = new byte[maxBufferLength];
            fis.read(buffer);
            crc.update(buffer);
        }
        int reminder = fis.available();
        if (reminder>0 && reminder<maxBufferLength){
            buffer = new byte[reminder];
            fis.read(buffer);
            crc.update(buffer);
        }

        return crc;
    }
}
