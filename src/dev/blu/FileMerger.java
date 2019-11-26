package dev.blu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileMerger extends FileHandler {
    private File f;

    public FileMerger(File f) {
        setFile(f);
    }

    public File getFile() {
        return f;
    }

    protected void setFile(File f1) {
        this.f = f1;
    }

    public int merge() throws IOException {
        String abs = getFile().getAbsolutePath();                                           //C:\files\photo.png
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
