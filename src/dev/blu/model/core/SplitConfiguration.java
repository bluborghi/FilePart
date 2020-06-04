package dev.blu.model.core;

import java.io.File;
import java.util.UUID;

import dev.blu.model.enums.ByteUnit;

public class SplitConfiguration {
	private int partNumber;
	private long partSize;
	private ByteUnit unit;
	private char[] pw;
	private String outputDir;
	private UUID id;
	
	public SplitConfiguration(UUID id,int partNumber, long partSize, ByteUnit unit, char[] pw,
			String outputDir) {
		this.id = id;
		this.partNumber = partNumber;
		this.partSize = partSize;
		this.unit = unit;
		this.pw = pw;
		this.outputDir = outputDir;
	}
	
	public SplitConfiguration(UUID id) {
		this(id, 0, 0, ByteUnit.MiB, new char[0] , "");
	}
	
	public int getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(int partNumber) {
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
