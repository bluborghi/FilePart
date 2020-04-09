package dev.blu.model.interfaces;

import java.io.File;


public interface FileAction {
	public void start();
	public double getPercentage();
	public String checkForErrors();
	public File getInputFile();
	public File getOutputFile();
}
