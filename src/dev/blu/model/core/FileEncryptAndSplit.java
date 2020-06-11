package dev.blu.model.core;

import java.io.File;
import java.io.IOException;

import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.interfaces.FileAction;

public class FileEncryptAndSplit implements FileAction {
	private FileAction encrypt;
	private FileAction split;
	private File inputFile;
	private File toDelete;
	private ProcessStatus status;
	private boolean stop = false;
	
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
		toDelete = encrypt.getOutputFile();
		status = ProcessStatus.Ready;
	}
	
	public FileEncryptAndSplit(FileConfiguration conf) {
		this(conf.getFile(), conf.getSplitConfig());
	}

	@Override
	public void start() {
		status = ProcessStatus.Running;
		encrypt.start();
		split.start();
		if (stop ) {
			status = ProcessStatus.Stopped;
		}else {
			status = ProcessStatus.Completed;
		}		
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
	public File getInputFile() {
		return inputFile;
	}

	@Override
	public File getOutputFile() {
		return split.getOutputFile();
	}

	@Override
	public void clear() {
		toDelete.delete();
	}

	@Override
	public void stopAction() {
		status = ProcessStatus.Stopping;
		stop = true;
		if (encrypt.getActionStatus() == ProcessStatus.Running) {
			encrypt.stopAction();
		}
		if (split.getActionStatus() == ProcessStatus.Running) {
			split.stopAction();
		}
	}

	@Override
	public ProcessStatus getActionStatus() {
		return status;
	}
}
