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
public class FileMergeAndDecrypt implements FileAction {
	private FileAction merge;
	private FileAction decrypt;
	private File inputFile;
	private File toDelete;
	private ProcessStatus status;
	private boolean stop = false;
	
	/**
	 * Initializes {@link FileMergeAndDecrypt} using an input {@link File} and a {@link FileActionConfiguration}
	 * @param inputFile The {@link File} to merge and decrypt
	 * @param params The {@link FileActionConfiguration} parameters
	 */
	public FileMergeAndDecrypt(File inputFile, FileActionConfiguration params) {
		this.inputFile  = inputFile;
		merge = new FileMerger(inputFile,params);
		try {
			new File(merge.getOutputFile().getAbsolutePath()).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		decrypt = new FileDecryptor(merge.getOutputFile(), params);
		toDelete = merge.getOutputFile();
		status = ProcessStatus.Ready;
	}
	
	/**
	 * Initializes {@link FileMergeAndDecrypt} using a {@link FileConfiguration}
	 * @param conf The {@link FileConfiguration}
	 */
	public FileMergeAndDecrypt(FileConfiguration conf) {
		this(conf.getFile(),conf.getActionConfig());
	}

	@Override
	public void start() {
		status = ProcessStatus.Running;
		merge.start();
		if (!stop)
			decrypt.start();
		
		if (stop) {
			status = ProcessStatus.Stopped;
		}
		else {
			status = ProcessStatus.Completed;			
		}
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

	@Override
	public void stopAction() {
		status = ProcessStatus.Stopping;
		stop = true;
		if (merge.getActionStatus() == ProcessStatus.Running) {
			merge.stopAction();
		}
		if (decrypt.getActionStatus() == ProcessStatus.Running) {
			decrypt.stopAction();
		}
	}

	@Override
	public ProcessStatus getActionStatus() {
		return status;
	}

}
