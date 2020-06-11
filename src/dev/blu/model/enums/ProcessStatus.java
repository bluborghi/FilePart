package dev.blu.model.enums;

public enum ProcessStatus {
	Ready("Ready"),
	Waiting("Waiting"),
	Running("Running"),
	Error("Error"),
	Completed("Completed"), 
	Stopping("Stopping"), 
	Stopped("Stopped");
	
	private String name;
	
	private ProcessStatus(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
