package dev.blu;

import java.io.*;

public class FilePartitioner {

    public static int split(String dir, int parts) throws IOException {
        File f = new File(dir);
        return split(f,parts);
    }

    public static int split(File f, int parts) throws IOException {

        int part_length = (int) (f.length() / parts) + 1;
        FileInputStream fis = new FileInputStream(f);

        int i = 1;
        byte[] buffer = new byte[part_length];
        while (i<=parts) {
            if (i<parts)
                fis.read(buffer);
            else
                buffer = fis.readAllBytes(); //the last partition is smaller than part_length
            File outputFile = new File(f.getAbsolutePath() + "." + String.format("%0" + String.valueOf(parts-1).length() + "d", i));
            i++;
            outputFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(buffer);
            fos.close();
        }
        fis.close();
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

    public static int merge(String dir) throws IOException {
        File f = new File(dir);
        return merge(f);
    }

    public static int merge(File f1) throws IOException {
        String abs = f1.getAbsolutePath();                                           //C:\files\photo.png
        File starter = null;
        int MAX_LENGTH = String.valueOf(Integer.MAX_VALUE).length();
        for (int i=1; i<=MAX_LENGTH; i++){
            File tmp = new File(abs+"."+String.format("%0" + i + "d", 1));
            if (tmp.exists()){
                starter = tmp;
                break;
            }
        }
        if (starter==null) return -1;

        String full = starter.getAbsolutePath();                        //C:\files\photo.png.001
        String name = starter.getName();                                //photo.png.001
        String ext = getFileExtension(name);                            //001
        String prefix = removeFileExtension(name);                      //photo.png
        String parent = getParentDirectory(full)+"\\";                  //C:\files\

        File output = new File(parent +"_"+ prefix);          //C:\files\_photo.png
        output.createNewFile();
        FileOutputStream os = new FileOutputStream(output);
        File tmp;
        int i = 1;
        while ((tmp = new File(parent + prefix + "." + String.format("%0" + ext.length() + "d", i))).exists()) { //C:\files\photo.png.i
            FileInputStream is = new FileInputStream(tmp);
            byte[] bytes = is.readAllBytes();
            os.write(bytes);
            is.close();
            i++;
        }
        os.close();
        return 0;
    }

    private static String getParentDirectory(String dir) {
        return new File(dir).getAbsoluteFile().getParent();
    }

    private static String getFileExtension(String fileName) { //boh se no c'è string tokenizer
        if (hasExtension(fileName))
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else
            return "";
    }

    private static String removeFileExtension(String fileName) {
        if (hasExtension(fileName))
            return fileName.substring(0, fileName.lastIndexOf("."));
        else
            return fileName;
    }

    private static boolean hasExtension(String fileName) {
        return fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0;
    }
}


