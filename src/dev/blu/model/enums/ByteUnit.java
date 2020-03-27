package dev.blu.model.enums;

public enum ByteUnit {
	B(1),
	KiB(1024),
	MiB(1024*1024),
	GiB(1024*1024*1024);
	
	private long multiplier;
	
	private ByteUnit(long multiplier) {
		this.multiplier = multiplier;
	}
	
	public long getMultiplier() {
		return multiplier;
	}
	
}
