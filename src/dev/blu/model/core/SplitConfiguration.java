package dev.blu.model.core;

import java.io.File;
import java.util.UUID;

import dev.blu.model.enums.ByteUnit;
import dev.blu.model.enums.SplitOption;

public class SplitConfiguration {
	private SplitOption splitOption;
	private long partNumber;
	private long partSize;
	private ByteUnit unit;
	private char[] pw;
	private String outputDir;
	private UUID id;
	
	public SplitConfiguration(UUID id, SplitOption splitOption, int partNumber, long partSize, ByteUnit unit, char[] pw,
			String outputDir) {
		this.id = id;
		this.splitOption = splitOption;
		this.partNumber = partNumber;
		this.partSize = partSize;
		this.unit = unit;
		this.pw = pw;
		this.outputDir = outputDir;
	}
	
	public SplitConfiguration(UUID id) {
		this(id, SplitOption.DoNothing, -1, -1, ByteUnit.MiB, null , "");
	}

	public SplitOption getSplitOption() {
		return splitOption;
	}

	public void setSplitOption(SplitOption splitOption) {
		this.splitOption = splitOption;
	}

	public long getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(long partNumber) {
		this.partNumber = partNumber;
	}

	public long getPartSize() {
		return partSize;
	}

	public void setPartSize(long partSize) {
		this.partSize = partSize;
	}

	public ByteUnit getUnit() {
		return unit;
	}

	public void setUnit(ByteUnit unit) {
		this.unit = unit;
	}

	public char[] getPw() {
		return pw;
	}

	public void setPw(char[] cs) {
		this.pw = cs;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public UUID getId() {
		return id;
	}

}
