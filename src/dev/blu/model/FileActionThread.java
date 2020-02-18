package dev.blu.model;

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
}
