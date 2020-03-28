package dev.blu.model.core;

import java.io.File;
import java.util.UUID;
import java.util.Vector;

import dev.blu.model.enums.ProcessStatus;

public class FileConfiguration {
	private File file;
	private UUID id;
	private ProcessStatus state;
	private int percentage;
	private SplitConfiguration splitConfig;
	
	public FileConfiguration(File file, UUID id, ProcessStatus state,
			SplitConfiguration splitConfig) {
		this.file = file;
		this.id = id;
		this.state = state;
		this.percentage = 0;
		this.splitConfig = splitConfig;
	}
	
	public FileConfiguration(File file, UUID id) {
		this.file = file;
		this.id = id;
		this.state = ProcessStatus.Ready;
		this.percentage = 0;
		this.splitConfig = new SplitConfiguration(id);
	}

	public FileConfiguration(File file) {
		this(file, UUID.randomUUID());
	}
	
	public File getFile() {
		return file;
	}

	public UUID getId() {
		return id;
	}

	public ProcessStatus getState() {
		return state;
	}

	public void setState(ProcessStatus state) {
		this.state = state;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		setPercentage((int) Math.round(percentage));
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public SplitConfiguration getSplitConfig() {
		return splitConfig;
	}

	public void setSplitConfig(SplitConfiguration splitConfig) {
		this.splitConfig = splitConfig;
	}
	
	
}
