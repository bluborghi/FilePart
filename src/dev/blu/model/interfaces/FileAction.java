package dev.blu.model.interfaces;

import java.io.File;

import dev.blu.model.enums.SplitOption;

public interface FileAction {
	public void start();
	public double getPercentage();
	public String checkForErrors();
	public File getFile();
	public SplitOption getSplitOption();

}
