package dev.blu;

import java.io.*;

public class FilePartitioner {
    public static int divide(File f, int parts) throws IOException {

        int part_length = (int) (f.length()/parts)+1;
        FileInputStream fis = new FileInputStream(f);


        int i = 0;
        byte[] buffer = new byte[part_length];
        while (fis.read(buffer)!=-1){
            File outputFile = new File(f.getAbsolutePath()+f.getName()+"_"+ i +".part");
            i++;
            outputFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(buffer);
            fos.close();
        }
        fis.close();
        return 0;
    }
}


