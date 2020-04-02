package dev.blu.model.enums;


public enum SplitOption {
	SplitByPartNumber("Split by part number"),
	SplitByMaxSize("Split by max size"),
	SplitAndEncrypt("Split and encrypt"),
	Merge("Merge"),
	MergeAndDecrypt("Merge and decrypt"),
	DoNothing("Do nothing"),
	Encrypt("Encrypt"), 
	Decrypt("Decrypt");
	
	private String name;
	
	private SplitOption(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
