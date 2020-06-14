package dev.blu.model.enums;

/**
 * The byte unit matched with its integer multiplier
 * @author blubo
 *
 */
public enum ByteUnit {
	B(1),
	KiB(1024),
	MiB(1024*1024),
	GiB(1024*1024*1024);
	
	private long multiplier;
	
	private ByteUnit(long multiplier) {
		this.multiplier = multiplier;
	}
	
	/**
	 * Gets the multiplier of a {@link ByteUnit}
	 * @return The multiplier
	 */
	public long getMultiplier() {
		return multiplier;
	}
	
}
