package dev.blu.model.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;

import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.helpers.FileHelper;
import dev.blu.model.interfaces.FileAction;

import static java.time.temporal.ChronoUnit.MILLIS;

/**
 * Merge the partitions of a file together and rebuilds the original file
 * @author blubo
 *
 */
public class FileMerger implements FileAction{
    protected File f;
    protected long[] bytesTransfered = new long[1];
	private long totalBytes;
	private String outputDir;
	private ProcessStatus status;
	private boolean[] stop = {false};//returns stop as an array (pointer workaround)
	
	/**
	 * Initializes {@link FileMerger} using a {@link FileConfiguration}
	 * @param config The {@link FileConfiguration}
	 */
    public FileMerger(FileConfiguration config) {
    	setFile(config.getFile());
        setOutputDir(config.getActionConfig().getOutputDir());
        
        bytesTransfered[0] = 0;
        totalBytes = 0;
        
        status = ProcessStatus.Ready;
    }

	/**
	 * Initializes {@link FileMerger} using an input {@link File} and a {@link FileActionConfiguration}
	 * @param inputFile The first (.001) {@link File} partition to merge
	 * @param params The {@link FileActionConfiguration} parameters
	 */
    public FileMerger(File inputFile, FileActionConfiguration params) {
    	setFile(inputFile);
        setOutputDir(params.getOutputDir());
        
        bytesTransfered[0] = 0;
        totalBytes = 0;
        
        status = ProcessStatus.Ready;
	}

    /**
     * Merge the group of files specified
     * @throws IOException I/O error
     */
	public void merge() throws IOException {
    	if (getTotalBytes() == 0) calcTotalBytes();
        
    	File starter = getInputFile();
        if (starter == null) return;

        String full = starter.getAbsolutePath();                        //C:\files\photo.png.001
        String ext = FileHelper.getFileExtension(full);                            //001
        String commonPath = FileHelper.removeFileExtension(full);	//C:\files\photo.png

        System.out.println(getOutputDir()+File.separator+FileHelper.removeFileExtension(starter.getName()));
        File output = new File(getOutputDir()+File.separator+FileHelper.removeFileExtension(starter.getName())); // outputDir\photo.png
        output.createNewFile();
        FileOutputStream fos = new FileOutputStream(output);
        File tmp;
        int i = 1;
        while ((tmp = new File(commonPath + "." + String.format("%0" + ext.length() + "d", i))).exists()) { //C:\files\photo.png.i
            FileInputStream fis = new FileInputStream(tmp);
            FileHelper.transfer(fis, fos, tmp.length(),bytesTransfered,stop);
            
            fis.close();
            i++;
        }
        fos.close();
        
        if (stop[0])
        	output.delete();
    }

	private void setStop(boolean stop) { 
		this.stop[0] = stop;
	}

	/**
	 * Gets the first input {@link File}
	 * @return The input {@link File}
	 */
	public File getInputFile() {
        return f;
    }

    protected void setFile(File f1) {
        this.f = f1;
    }
    
    /**
	 * Gets the output file directory
	 * @return the output directory
	 */
	public String getOutputDir() {
		return outputDir;
	}

	private void setOutputDir(String outputDir) {
		if (outputDir == null || outputDir.isEmpty()) {
			this.outputDir = getInputFile().getParent();
		}
		else {			
			this.outputDir = outputDir;
		}
	}

	@Override
	public void start() {
		status = ProcessStatus.Running;
		try {
			merge();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (stop[0]) {
			status = ProcessStatus.Stopped;
		} else {
			status = ProcessStatus.Completed;			
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
		File starter = getInputFile();
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
		if (getInputFile() == null || !getInputFile().exists() || getInputFile().isDirectory())
			errors = errors.concat("File not found or invalid").concat(System.lineSeparator());
		File outputDir = null;
		if (getOutputDir() != null) outputDir = new File(getOutputDir());
		if (outputDir == null || !outputDir.exists() || !outputDir.isDirectory())
			errors = errors.concat("Output directory not found or invalid").concat(System.lineSeparator());
		calcTotalBytes();
		if (getTotalBytes() <= 0)
			errors = errors.concat("Error reading input files").concat(System.lineSeparator());
		
		return errors;
	}

	@Override
	public File getOutputFile() {
		return new File(getOutputDir()+File.separator+FileHelper.removeFileExtension(getInputFile().getName()));   
	}
    
	@Override
	public void clear() {
		//nothing to clear
	}


	@Override
	public void stopAction() {
		status = ProcessStatus.Stopping;
		setStop(true);
	}


	@Override
	public ProcessStatus getActionStatus() {
		return status;
	}
    
}

