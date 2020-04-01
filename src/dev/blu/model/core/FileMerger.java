package dev.blu.model.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;

import dev.blu.model.enums.SplitOption;
import dev.blu.model.helpers.FileHelper;
import dev.blu.model.interfaces.FileAction;

import static java.time.temporal.ChronoUnit.MILLIS;

public class FileMerger implements FileAction{
    protected File f;
    protected int bufferLength;
    protected long[] bytesTransfered = new long[1];
	private long totalBytes;
	private String outputDir;
	
    public FileMerger(FileConfiguration config) {
    	setFile(config.getFile());
        setBufferLength(0);//use default
        setOutputDir(config.getSplitConfig().getOutputDir());
        
        bytesTransfered[0] = 0;
        totalBytes = 0;
    }
    

    public int merge() throws IOException {
    	if (getTotalBytes() == 0) calcTotalBytes();
        /*String abs = getFile().getAbsolutePath();                                           //C:\files\photo.png
        File starter = null;
        int MAX_LENGTH = String.valueOf(Integer.MAX_VALUE).length();
        for (int i = 1; i <= MAX_LENGTH; i++) {
            File tmp = new File(abs + "." + String.format("%0" + i + "d", 1));
            if (tmp.exists()) {
                starter = tmp;
                break;
            }
        }*/
    	File starter = getFile();
        if (starter == null) return -1;

        String full = starter.getAbsolutePath();                        //C:\files\photo.png.001
        String ext = FileHelper.getFileExtension(full);                            //001
        String commonPath = FileHelper.removeFileExtension(full);	//C:\files\photo.png

        // String name = starter.getName();                                //photo.png.001
       //String prefix = FileHelper.removeFileExtension(name);                      //photo.png
       // String parent = FileHelper.getParentDirectory(full) + File.pathSeparator; //C:\files\
       
        System.out.println(getOutputDir()+File.separator+FileHelper.removeFileExtension(starter.getName()));
        File output = new File(getOutputDir()+File.separator+FileHelper.removeFileExtension(starter.getName())); // outputDir\photo.png
        output.createNewFile();
        FileOutputStream fos = new FileOutputStream(output);
        File tmp;
        int i = 1;
        while ((tmp = new File(commonPath + "." + String.format("%0" + ext.length() + "d", i))).exists()) { //C:\files\photo.png.i
            FileInputStream fis = new FileInputStream(tmp);

            if (getBufferLength()==0) //if bufferLength is set to default (0)
                FileHelper.transfer(fis, fos, tmp.length(),bytesTransfered);
            else
                FileHelper.transfer(fis,fos,tmp.length(),getBufferLength(),bytesTransfered);

            fis.close();
            i++;
        }
        fos.close();
        return 0;
    }

    public File getFile() {
        return f;
    }

    protected void setFile(File f1) {
        this.f = f1;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        if (bufferLength<0)
            this.bufferLength = 0; //if invalid, set to default
        else
            this.bufferLength = bufferLength;
    }
    
	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		if (outputDir.isEmpty())
			this.outputDir = getFile().getParent();
		this.outputDir = outputDir;
	}

	@Override
	public void start() {
		try {
			merge();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double getPercentage() {
		long tot = getTotalBytes();
		if (tot != 0)
			return ((double)bytesTransfered[0]/tot)*100;
		else 
			return 0;
	}

	private long getTotalBytes() {
		return totalBytes;
	}

	private void calcTotalBytes() {
		totalBytes = 0;
		File starter = getFile();
        if (starter == null) return;
        
        String full = starter.getAbsolutePath();                        //C:\files\photo.png.001
        String ext = FileHelper.getFileExtension(full);                            //001
        String commonPath = FileHelper.removeFileExtension(full);	//C:\files\photo.png
       
        int i = 1;
        File tmp;
		while ((tmp = new File(commonPath + "." + String.format("%0" + ext.length() + "d", i))).exists()) {//this means commonPath.00i        
			this.totalBytes += tmp.length();
			//System.out.println("reading file " + tmp.getName() + " of size " + tmp.length() + "B for a total of " +totalBytes+"B");
			i++;
		}
	}

	@Override
	public String checkForErrors() {
		String errors = "";
		if (getFile() == null || !getFile().exists() || getFile().isDirectory())
			errors = errors.concat("File not found or invalid").concat(System.lineSeparator());
		File outputDir = new File(getOutputDir());
		if (outputDir == null || !outputDir.exists() || !outputDir.isDirectory())
			errors = errors.concat("Output directory not found or invalid").concat(System.lineSeparator());
		calcTotalBytes();
		if (getTotalBytes() <= 0)
			errors = errors.concat("Error reading input files").concat(System.lineSeparator());
		
		return errors;
	}

	@Override
	public SplitOption getSplitOption() {
		return SplitOption.Merge;
	}
    
    
    
}
