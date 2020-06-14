package dev.blu.model.enums;

import dev.blu.model.interfaces.FileAction;

/**
 * The process status of a {@link FileAction}
 * @author blubo
 *
 */
public enum ProcessStatus {
	Ready("Ready"),
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
