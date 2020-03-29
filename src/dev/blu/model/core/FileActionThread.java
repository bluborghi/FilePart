package dev.blu.model.core;

import java.io.File;

import dev.blu.model.interfaces.FileAction;

public class FileActionThread extends Thread {
	FileAction fa;
	public FileActionThread(FileAction fa) {
		this.fa = fa;
	}
	
	@Override
	public void run() {
		fa.start();
	}
	
	public double getPercentage() {
		return fa.getPercentage();
	}
	
	public File getFile() {
		return fa.getFile();
	}
}
