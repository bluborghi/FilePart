package dev.blu.model.interfaces;

import java.io.File;

import dev.blu.model.enums.ProcessStatus;


public interface FileAction {
	public void start();
	public void clear();
	public double getPercentage();
	public String checkForErrors();
	public File getInputFile();
	public File getOutputFile();
	public void stopAction();
	public ProcessStatus getActionStatus();
}
