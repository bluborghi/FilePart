package dev.blu.model.core;

import java.io.File;

/**
 * Splits a file in the specified number of parts
 * @author blubo
 *
 */
public class FileSplitterByPartNumber extends FileSplitterByMaxSize {
    private long parts;

    /**
	 * Initializes {@link FileSplitterByPartNumber} using a {@link FileConfiguration}
	 * @param config The {@link FileConfiguration}
	 */
    public FileSplitterByPartNumber(FileConfiguration config) {
        super(config);
        setParts(config.getActionConfig().getPartNumber());
        long size = calcMaxSize();
        setMaxSize(size);
    }

    /**
	 * Initializes {@link FileSplitterByMaxSize} using an input {@link File} and a {@link FileActionConfiguration}
	 * @param inputFile The {@link File} to split
	 * @param params The {@link FileActionConfiguration} parameters
	 */
    public FileSplitterByPartNumber(File inputFile, FileActionConfiguration params) {
    	super(inputFile,params);
    	setParts(params.getPartNumber());
        long size = calcMaxSize();
        setMaxSize(size);
	}

    /**
     * Gets the number of output parts
     * @return The number of parts
     */
	public long getParts() {
        return parts;
    }

    protected void setParts(long parts) {
        this.parts = parts;
    }

    /**
     * Calculates the maximum file size in order to use parent's primitives
     * @return the maximum file size in order to divide the input {@link File} in the desired number of parts
     */
    protected long calcMaxSize(){
        long file_length = getInputFile().length();
        if (parts <= 0)
        	return 0;
        return (file_length+parts-1)/parts;//formula to round up an integer division (positive only members)
    }
    
    @Override
	public String checkForErrors() {
		String errors = "";
		if (getInputFile() == null)
			errors = errors.concat("File not specified").concat(System.lineSeparator());
		else if (!getInputFile().exists()) {
			errors = errors.concat("File not found").concat(System.lineSeparator());
		}
		if (getBufferLength()<= 0)
			errors = errors.concat("Invalid buffer length").concat(System.lineSeparator());
		if (getParts() <= 0)
			errors = errors.concat("Invalid number of parts").concat(System.lineSeparator());
		if (!getOutputDir().isDirectory()) {
			errors = errors.concat("Invalid destination path").concat(System.lineSeparator());
		}
	
		return errors;
	}
}
