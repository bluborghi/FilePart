package dev.blu.model.core;

import java.io.File;

import dev.blu.model.enums.SplitOption;

public class FileSplitterByPartNumber extends FileSplitterByMaxSize {
    private long parts;

    public FileSplitterByPartNumber(FileConfiguration config) {
        super(config);
        setParts(config.getSplitConfig().getPartNumber());
        long size = calcMaxSize();
        setMaxSize(size);
    }

    public FileSplitterByPartNumber(File inputFile, SplitConfiguration params) {
    	super(inputFile,params);
    	setParts(params.getPartNumber());
        long size = calcMaxSize();
        setMaxSize(size);
	}

	public long getParts() {
        return parts;
    }

    protected void setParts(long parts) {
        this.parts = parts;
    }

    protected long calcMaxSize(){
        long file_length = getFile().length();
        if (parts <= 0)
        	return 0;
        return (file_length+parts-1)/parts;//formula to round up an integer division (positive only members)
    }
    
    @Override
	public String checkForErrors() {
		String errors = "";
		if (getFile() == null)
			errors = errors.concat("File not specified").concat(System.lineSeparator());
		else if (!getFile().exists()) {
			errors = errors.concat("File not found").concat(System.lineSeparator());
		}
		if (getBufferLength()<= 0)
			errors = errors.concat("Invalid buffer length").concat(System.lineSeparator());
		if (getParts() <= 0)
			errors = errors.concat("Invalid number of parts").concat(System.lineSeparator());
		if (!getDestinationDirectory().isDirectory()) {
			errors = errors.concat("Invalid destination path").concat(System.lineSeparator());
		}
	
		return errors;
	}
    
    @Override
	public SplitOption getSplitOption() {
		return SplitOption.SplitByPartNumber;
	}
}
