package dev.blu.model.core;

import java.io.File;
import java.io.IOException;

import dev.blu.model.interfaces.FileAction;

public class FileMergeAndDecrypt implements FileAction {
	private FileAction merge;
	private FileAction decrypt;
	private File inputFile;
	private File toDelete;
	
	public FileMergeAndDecrypt(File inputFile, SplitConfiguration params) {
		this.inputFile  = inputFile;
		merge = new FileMerger(inputFile,params);
		try {
			new File(merge.getOutputFile().getAbsolutePath()).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		decrypt = new FileDecryptor(merge.getOutputFile(), params);
		toDelete = merge.getOutputFile();
	}
	
	public FileMergeAndDecrypt(FileConfiguration conf) {
		this(conf.getFile(),conf.getSplitConfig());
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
	public File getInputFile() {
		return inputFile;
	}

	@Override
	public File getOutputFile() {
		return decrypt.getOutputFile();
	}

	@Override
	public void clear() {
		toDelete.delete();
	}

}
