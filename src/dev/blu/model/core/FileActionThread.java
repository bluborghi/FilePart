package dev.blu.model.core;

import java.io.File;
import java.util.UUID;

import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.interfaces.FileAction;

/**
 * Thread that runs a {@link FileAction}
 * @author blubo
 *
 */
public class FileActionThread extends Thread {
	private FileAction fa;
	private String error;
	private UUID id;
	
	/**
	 * Instances a FileActionThread with an error message, this thread won't start
	 * 
	 * @param fa FileAction to run in the thread
	 * @param err Error message, if not empty the thread won't start
	 * @param id  The identifier of the FileConfiguration
	 */
	public FileActionThread(FileAction fa, String err, UUID id) {
		this.fa = fa;
		this.error = err;
		this.id = id;
	}
	
	/**
	 * Instances a FileActionThread
	 * 
	 * @param fa FileAction to run in the thread
	 * @param id  The identifier of the FileConfiguration
	 */
	public FileActionThread(FileAction fa, UUID id) {
		this(fa,"", id);
	}
	
	@Override
	public void run() {
		fa.start();
		fa.clear();
	}
	
	/**
	 * Gets the progress of the FileAction
	 * @return a double in the [0-100] range
	 */
	public double getPercentage() {
		return fa.getPercentage();
	}
	
	/**
	 * Gets the input file of the FileAction 
	 * @return The input file
	 */
	public File getFile() {
		return fa.getInputFile();
	}
	
	/**
	 * Gets the error message
	 * @return The error message
	 */
	public String getErrorMessage() {
		return error;
	}
	
	public boolean hasErrors() {
		return (error!=null && !error.isEmpty())
				|| fa==null;
	}
	
	
	/**
	 * stops the FileAction while running
	 */
	public void stopAction() {
		fa.stopAction();
	}
	
	/**
	 * Gets the current status of the FileAction
	 * @return ProcessStatus of the FileAction
	 */
	public ProcessStatus getActionStatus() {
		return fa.getActionStatus();
	}
	
	/**
	 * Gets the id of the FileConfiguration
	 * @return The id of the FileConfiguration
	 */
	public UUID getActionId() {
		return id;
	}
}
