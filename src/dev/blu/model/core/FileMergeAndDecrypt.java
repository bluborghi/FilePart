package dev.blu.model.core;

import java.io.File;
import java.io.IOException;

import dev.blu.model.enums.SplitOption;
import dev.blu.model.interfaces.FileAction;

public class FileMergeAndDecrypt implements FileAction {
	FileAction merge;
	FileAction decrypt;
	File inputFile;
	
	public FileMergeAndDecrypt(File inputFile, SplitConfiguration params) {
		this.inputFile  = inputFile;
		merge = new FileMerger(inputFile,params);
		try {
			new File(merge.getOutputFile().getAbsolutePath()).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		decrypt = new FileDecryptor(merge.getOutputFile(), params);
	}
	
	@Override
	public void start() {
		merge.start();
		decrypt.start();
	}

	@Override
	public double getPercentage() {
		return (merge.getPercentage() + decrypt.getPercentage())/2;
	}

	@Override
	public String checkForErrors() {
		String err = "";
		err = err.concat(merge.checkForErrors());
		err = err.concat(decrypt.checkForErrors());
		return err;
	}

	@Override
	public File getFile() {
		return inputFile;
	}

	@Override
	public SplitOption getSplitOption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getOutputFile() {
		return decrypt.getOutputFile();
	}

}
