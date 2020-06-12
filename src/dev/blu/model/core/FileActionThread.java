package dev.blu.model.core;

import java.io.File;
import java.util.UUID;

import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.interfaces.FileAction;

public class FileActionThread extends Thread {
	private FileAction fa;
	private String error;
	private UUID id;
	
	public FileActionThread(FileAction fa, String err, UUID id) {
		this.fa = fa;
		this.error = err;
		this.id = id;
	}
	
	public FileActionThread(FileAction fa, UUID id) {
		this(fa,"", id);
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
	
	public void stopAction() {
		fa.stopAction();
	}
	
	public ProcessStatus getActionStatus() {
		return fa.getActionStatus();
	}
	
	public UUID getActionId() {
		return id;
	}
}
