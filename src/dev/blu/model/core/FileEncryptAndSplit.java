package dev.blu.model.core;

import java.io.File;
import java.io.IOException;

import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.interfaces.FileAction;

/**
 * A composite {@link FileAction} that uses {@link FileEncryptor} and {@link FileSplitter} to perform an encrypted division of a {@link File}
 * @author blubo
 *
 */
public class FileEncryptAndSplit implements FileAction {
	private FileAction encrypt;
	private FileAction split;
	private File inputFile;
	private File toDelete;
	private ProcessStatus status;
	private boolean stop = false;
	
	/**
	 * Initializes {@link FileEncryptAndSplit} using an input {@link File} and a {@link FileActionConfiguration}
	 * @param inputFile The {@link File} to encrypt and split
	 * @param params The {@link FileActionConfiguration} parameters
	 */
	public FileEncryptAndSplit(File inputFile, FileActionConfiguration params) {
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
	
	/**
	 * Initializes {@link FileEncryptAndSplit} using a {@link FileConfiguration}
	 * @param conf The {@link FileConfiguration}
	 */
	public FileEncryptAndSplit(FileConfiguration conf) {
		this(conf.getFile(), conf.getActionConfig());
	}

	@Override
	public void start() {
		status = ProcessStatus.Running;
		encrypt.start();
		if (!stop)
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
