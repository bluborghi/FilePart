package dev.blu.model.core;

import java.io.File;

import dev.blu.model.interfaces.FileAction;

public class FileActionThread extends Thread {
	private FileAction fa;
	private String error;
	
	public FileActionThread(FileAction fa, String err) {
		this.fa = fa;
		this.error = err;
	}
	
	public FileActionThread(FileAction fa) {
		this(fa,"");
	}
	
	@Override
	public void run() {
		fa.start();
		fa.clear();
	}
	
	public double getPercentage() {
		return fa.getPercentage();
	}
	
	public File getFile() {
		return fa.getInputFile();
	}
	
	public String getErrorMessage() {
		return error;
	}
	
	public boolean hasErrors() {
		return (error!=null && !error.isEmpty())
				|| fa==null;
	}
}
