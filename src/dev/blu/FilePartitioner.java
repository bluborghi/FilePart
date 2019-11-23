package dev.blu;

import java.io.*;

public class FilePartitioner {

    public static int splitByNumberOfParts(String dir, int parts) throws IOException {
        File f = new File(dir);
        return splitByNumberOfParts(f, parts);
    }

    public static int splitByNumberOfParts(File f, int parts) throws IOException {
        long file_length = f.length();
        int part_length = (int) ((file_length - 1) / parts) + 1;
        int last_part_length = (int) (file_length - part_length * (parts - 1));
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


    private static void transfer(FileInputStream fis, FileOutputStream fos, long part_length) throws IOException {
        int MAX_BUF_LENGTH = 1024 * 1024 * 128; //128MB
        int buffer_length;
        int last_buffer_length;
        byte[] buffer;
        int numberOfTransfers;

        if (part_length <= MAX_BUF_LENGTH) {
            last_buffer_length = (int) part_length;
            numberOfTransfers = 1;
        } else {
            buffer_length = MAX_BUF_LENGTH;
            numberOfTransfers = (int) ((part_length - 1) / buffer_length) + 1;
            last_buffer_length = (int) (part_length - buffer_length * (numberOfTransfers - 1));
            buffer = new byte[buffer_length];
            for (int i = numberOfTransfers; i > 1; i--) { //only if there's more than one part (you need to transfer more bytes than the buffer size)
                fis.read(buffer);
                fos.write(buffer);
            }
        }
        buffer = new byte[last_buffer_length];
        fis.read(buffer);
        fos.write(buffer);
    }


    /*
        public static int splitByMaxFileDimension(File f, long maxSize) throws FileNotFoundException {
            int parts = (int) (f.length()/maxSize)+1;


            FileInputStream fis = new FileInputStream(f);
            int extension_length = String.valueOf(parts-1).length();
            if (extension_length < 3) extension_length = 3;

            int i = 1;
            byte[] buffer = new byte[part_length];
            while (i<=parts) {
                if (i<parts)
                    fis.read(buffer);
                else
                    buffer = fis.readAllBytes(); //the last partition can be smaller than part_length
                File outputFile = new File(f.getAbsolutePath() + "." + String.format("%0" + extension_length  + "d", i));
                i++;
                outputFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write(buffer);
                fos.close();
            }
            fis.close();
            return 0;
        }
    */
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

            /*   java.lang.OutOfMemoryError: Java heap space
            byte[] bytes = fis.readAllBytes();
            fos.write(bytes);
            */
            transfer(fis, fos, tmp.length());

            fis.close();
            i++;
        }
        fos.close();
        return 0;
    }







    /*public static int getNumberOfParts(String dir) {
        File f = new File(dir);
        String name = f.getName();
        String ext = getFileExtension(name);
        String prefix = removeFileExtension(name);

        int i=0;
        boolean exists;
        do {
            i++;
            exists = new File(prefix + "." + String.format("%0" + ext.length() + "d", i)).exists();
        } while (exists);
        int numberOfParts = i-1;

        return numberOfParts;
    }*/
}


