package dev.blu.model.GUI.enums;

public enum ActionType {
	SplitByNumberOfParts("Split by number of parts"),
	SplitByMaxSize("Split by file size"),
	Merge("Merge");
	
	private String name;
	
	private ActionType(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
