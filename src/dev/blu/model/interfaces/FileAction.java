package dev.blu.model.interfaces;

import java.io.File;

import dev.blu.model.enums.ProcessStatus;

/**
 * The interface that describes an "action"
 * @author blubo
 *
 */
public interface FileAction {
	/**
	 * The action gets executed
	 */
	public void start();
	/**
	 * The action clears mid-step tmp files
	 */
	public void clear();
	/**
	 * Gets the percentage of completion
	 * @return The percentage of completion
	 */
	public double getPercentage();
	/**
	 * Check if there's any error preventing the action to start
	 * @return The error string
	 */
	public String checkForErrors();
	/**
	 * Gets the input file
	 * @return The input file
	 */
	public File getInputFile();
	/**
	 * Gets the output file
	 * @return The output file
	 */
	public File getOutputFile();
	/**
	 * Stops the execution of the action
	 */
	public void stopAction();
	/**
	 * Gets the {@link FileAction} status
	 * @return The status
	 */
	public ProcessStatus getActionStatus();
}
