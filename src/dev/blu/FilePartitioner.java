package dev.blu;

import java.io.*;

public class FilePartitioner {

    public static int splitByNumberOfParts(String dir, long parts) throws IOException {
        File f = new File(dir);
        return splitByNumberOfParts(f, parts);
    }

    public static int splitByNumberOfParts(File f, long parts) throws IOException {
        long file_length = f.length();
        long part_length = (file_length+parts-1)/parts;//formula to round up an integer division (positive only members)
        return splitByMaxFileDimension(f,part_length);
    }

    private static void transfer(FileInputStream fis, FileOutputStream fos, long part_length) throws IOException {
        int MAX_BUF_LENGTH = 1024 * 1024 * 64; //64MiB
        int buffer_length;
        int last_buffer_length;
        byte[] buffer;
        long numberOfTransfers;

        if (part_length > MAX_BUF_LENGTH) {
            buffer_length = MAX_BUF_LENGTH;
            numberOfTransfers = (int) ((part_length - 1) / buffer_length) + 1;
            last_buffer_length = (int) (part_length - buffer_length * (numberOfTransfers - 1));
            buffer = new byte[buffer_length];
            for (long i = numberOfTransfers; i > 1; i--) { //only if there's more than one part (you need to transfer more bytes than the buffer size)
                fis.read(buffer);
                fos.write(buffer);
            }
        } else {
            last_buffer_length = (int) part_length; //cast to int is ok (part_length <= MAX_BUF_LENGTH)
        }

        buffer = new byte[last_buffer_length];
        fis.read(buffer);
        fos.write(buffer);
    }

    public static int splitByMaxFileDimension(File f, long maxSize) throws IOException {
        long parts = (f.length() + maxSize - 1) / maxSize;//formula to round up an integer division (positive only members)
        long file_length = f.length();
        long part_length = maxSize;
        long last_part_length = (file_length - part_length * (parts - 1));
        FileInputStream fis = new FileInputStream(f);

        int extension_length = String.valueOf(parts - 1).length();
        if (extension_length < 3) extension_length = 3;


        int i = 1;
        while (i <= parts) {
            File outputFile = new File(f.getAbsolutePath() + "." + String.format("%0" + extension_length + "d", i));
            FileOutputStream fos = new FileOutputStream(outputFile);

            if (i < parts)
                transfer(fis, fos, part_length);
            else
                transfer(fis, fos, last_part_length);

            fos.close();
            i++;
        }
        fis.close();
        return 0;
    }

    public static int merge(String dir) throws IOException {
        File f = new File(dir);
        return merge(f);
    }

    public static int merge(File f1) throws IOException {
        String abs = f1.getAbsolutePath();                                           //C:\files\photo.png
        File starter = null;
        int MAX_LENGTH = String.valueOf(Integer.MAX_VALUE).length();
        for (int i = 1; i <= MAX_LENGTH; i++) {
            File tmp = new File(abs + "." + String.format("%0" + i + "d", 1));
            if (tmp.exists()) {
                starter = tmp;
                break;
            }
        }
        if (starter == null) return -1;

        String full = starter.getAbsolutePath();                        //C:\files\photo.png.001
        String name = starter.getName();                                //photo.png.001
        String ext = FileHelper.getFileExtension(name);                            //001
        String prefix = FileHelper.removeFileExtension(name);                      //photo.png
        String parent = FileHelper.getParentDirectory(full) + "\\";                  //C:\files\

        File output = new File(parent + "_" + prefix);          //C:\files\_photo.png
        output.createNewFile();
        FileOutputStream fos = new FileOutputStream(output);
        File tmp;
        int i = 1;
        while ((tmp = new File(parent + prefix + "." + String.format("%0" + ext.length() + "d", i))).exists()) { //C:\files\photo.png.i
            FileInputStream fis = new FileInputStream(tmp);

            transfer(fis, fos, tmp.length());

            fis.close();
            i++;
        }
        fos.close();
        return 0;
    }

}


