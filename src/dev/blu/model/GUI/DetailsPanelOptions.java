package dev.blu.model.GUI;

import dev.blu.model.GUI.enums.ActionType;
import dev.blu.model.enums.ByteUnit;

public class DetailsPanelOptions {
	private ActionType actionType;
//	private long maxSizeValue;
//	private ByteUnit maxSizeUnit;
//	private int partNumber;
//	private char[] password;
//	private String outputDir;
	
	
	public DetailsPanelOptions(ActionType at) {
		this.actionType = at;
//		this.maxSizeValue = 0;
//		this.maxSizeUnit = ByteUnit.MiB;
//		this.partNumber = 0;
//		this.password = new char[0];
//		this.outputDir = "";
	}
	
	public ActionType getActionType() {
		return actionType;
	}
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
//	public long getMaxSizeValue() {
//		return maxSizeValue;
//	}
//	public void setMaxSizeValue(long maxSizeValue) {
//		this.maxSizeValue = maxSizeValue;
//	}
//	public ByteUnit getMaxSizeUnit() {
//		return maxSizeUnit;
//	}
//	public void setMaxSizeUnit(ByteUnit maxSizeUnit) {
//		this.maxSizeUnit = maxSizeUnit;
//	}
//	public int getPartNumber() {
//		return partNumber;
//	}
//	public void setPartNumber(int partNumber) {
//		this.partNumber = partNumber;
//	}
//	public char[] getPassword() {
//		return password;
//	}
//	public void setPassword(char[] password) {
//		this.password = password;
//	}
//	public String getOutputDir() {
//		return outputDir;
//	}
//	public void setOutputDir(String outputDir) {
//		this.outputDir = outputDir;
//	}
//	
	
}
