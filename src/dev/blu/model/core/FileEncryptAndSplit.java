package dev.blu.model.core;

import java.io.File;
import java.io.IOException;

import dev.blu.model.enums.SplitOption;
import dev.blu.model.interfaces.FileAction;

public class FileEncryptAndSplit implements FileAction {
	FileAction encrypt;
	FileAction split;
	File inputFile;
	
	public FileEncryptAndSplit(File inputFile, SplitConfiguration params) {
		this.inputFile  = inputFile;
		encrypt = new FileEncryptor(inputFile,params);
		try {
			new File(encrypt.getOutputFile().getAbsolutePath()).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (params.getPartSize()>0)
			split = new FileSplitterByMaxSize(encrypt.getOutputFile(),params);
		else
			split = new FileSplitterByPartNumber(encrypt.getOutputFile(),params);
	}
	
	public FileEncryptAndSplit(FileConfiguration conf) {
		this(conf.getFile(), conf.getSplitConfig());
	}

	@Override
	public void start() {
		encrypt.start();
		split.start();
	}

	@Override
	public double getPercentage() {
		double first = encrypt.getPercentage();
		double second =  split.getPercentage();
		return (first + second)/2;
	}

	@Override
	public String checkForErrors() {
		String err = "";
		err = err.concat(encrypt.checkForErrors());
		err = err.concat(split.checkForErrors());
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
		return split.getOutputFile();
	}

}
