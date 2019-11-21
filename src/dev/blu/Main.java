package dev.blu;

import jdk.internal.util.xml.impl.Input;

import java.io.*;

public class Main {

    public static void main(String[] args)  {
        String fileDir = args[0];
        int parts = Integer.parseInt(args[1]);

        File inputFile = new File(fileDir);
        int part_length = (int) (inputFile.length()/parts)+1;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int i = 0;
        byte[] buffer = new byte[part_length];
        try {
            while (fis.read(buffer)!=-1){
                File outputFile = new File(inputFile.getAbsolutePath()+inputFile.getName()+"_"+ i +".part");
                i++;
                outputFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
