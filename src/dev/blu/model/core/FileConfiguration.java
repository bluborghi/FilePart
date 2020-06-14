package dev.blu.model.core;

import java.io.File;
import java.util.UUID;
import java.util.Vector;

import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.interfaces.FileAction;

/**
 * Describes the File that is going to be processed or is being processed in a {@link FileAction}
 * @author blubo
 *
 */
public class FileConfiguration {
	private File file;
	private UUID id;
	private ProcessStatus state;
	private int percentage;
	private FileActionConfiguration actionConfig;

	private FileConfiguration(File file, UUID id) {
		this.file = file;
		this.id = id;
		this.state = ProcessStatus.Ready;
		this.percentage = 0;
		this.actionConfig = null;
	}

	
	/**
	 * Initializes a new {@link FileConfiguration} and gives it a random generated {@link UUID} as id
	 * @param file The file to process in the {@link FileAction}
	 */
	public FileConfiguration(File file) {
		this(file, UUID.randomUUID());
	}

	/**
	 * Gets the file of the {@link FileConfiguration}
	 * @return The input file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Gets the id of the {@link FileConfiguration}
	 * @return The id of the configuration
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * Gets the state of the {@link FileConfiguration}
	 * @return The internal status as {@link ProcessStatus}
	 */
	public ProcessStatus getState() {
		return state;
	}

	/**
	 * Sets the state of the {@link FileConfiguration}
	 * @param state The {@link ProcessStatus}
	 */
	public void setState(ProcessStatus state) {
		this.state = state;
	}

	/** 
	 * Gets the progress of the process
	 * @return The percentage as {@link Double} in [0-100] range
	 */
	public double getPercentage() {
		return percentage;
	}

	/** 
	 * Sets the progress of the process
	 * @param percentage The percentage as {@link Double} in [0-100] range
	 */
	public void setPercentage(double percentage) {
		if (Double.isInfinite(percentage))
			setPercentage(100);
		else
			setPercentage((int) Math.round(percentage));
	}

	
	private void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	/**
	 * Gets the {@link FileActionConfiguration} of this {@link FileConfiguration}
	 * @return The {@link FileActionConfiguration}
	 */
	public FileActionConfiguration getActionConfig() {
		return actionConfig;
	}

	/**
	 * Sets the {@link FileActionConfiguration} of this {@link FileConfiguration}
	 * @param actionConfig The new {@link FileActionConfiguration}
	 */
	public void setActionConfig(FileActionConfiguration actionConfig) {
		this.actionConfig = actionConfig;
	}

}
