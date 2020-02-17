package dev.blu.model;

public class SplitConfiguration {
	private SplitOption splitOption;
	private long partNumber;
	private long partSize;
	private ByteUnit unit;
	private char[] pw;
	private String outputDir;

	public SplitConfiguration(SplitOption splitOption, int partNumber, long partSize, ByteUnit unit, char[] pw,
			String outputDir) {
		this.splitOption = splitOption;
		this.partNumber = partNumber;
		this.partSize = partSize;
		this.unit = unit;
		this.pw = pw;
		this.outputDir = outputDir;
	}
	
	public SplitConfiguration() {
		this(SplitOption.DoNothing, -1, -1, ByteUnit.MiB, null , "");
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

}
