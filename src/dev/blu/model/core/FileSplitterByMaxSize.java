package dev.blu.model.core;

import java.io.*;

import dev.blu.model.helpers.FileHelper;
import dev.blu.model.interfaces.FileAction;
import dev.blu.model.interfaces.FileSplitter;

public class FileSplitterByMaxSize implements FileSplitter, FileAction {
	protected File f;
	protected long maxSize;
	protected int bufferLength;
	protected long[] bytesTransfered = new long[1];
	protected File destinationDirectory;
	protected final static int DEF_BUF_LEN = 1 * 1024 * 1024; // 1 MiB

	private FileSplitterByMaxSize(File f, long maxSize, int bufferLength, String dest) {
		setFile(f);
		setMaxSize(maxSize);
		setBufferLength(bufferLength);
		bytesTransfered[0] = 0;

		if (dest == null || dest.isEmpty()) {
			if (f != null && f.getParentFile().isDirectory()) {
				dest = f.getParent();
			}
		}
		destinationDirectory = new File(dest);

	}

	private FileSplitterByMaxSize(File f, long maxSize, String dest) {
		this(f, maxSize, DEF_BUF_LEN, dest); // 1MiB standard buffer
	}

	public FileSplitterByMaxSize(FileConfiguration config) {
		this(config.getFile(),
				config.getSplitConfig().getPartSize() * config.getSplitConfig().getUnit().getMultiplier(),
				config.getSplitConfig().getOutputDir());
	}

	public FileSplitterByMaxSize(File inputFile, SplitConfiguration params) {
		this(inputFile, params.getPartSize() * params.getUnit().getMultiplier(), params.getOutputDir());
	}

	@Override
	public int split() {
		if (maxSize <= 0)
			return -1;
		long parts = (f.length() + maxSize - 1) / maxSize;// formula to round up an integer division (positive only
															// members)
		long file_length = f.length();
		long part_length = maxSize;
		long last_part_length = (file_length - part_length * (parts - 1));
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);

			int extension_length = String.valueOf(parts - 1).length();
			if (extension_length < 3)
				extension_length = 3;

			int i = 1;
			while (i <= parts) {
				File outputFile = new File(destinationDirectory.getAbsolutePath() + File.separator + f.getName() + "."
						+ String.format("%0" + extension_length + "d", i));
				FileOutputStream fos = null;
				fos = new FileOutputStream(outputFile);

				if (i < parts) {
					FileHelper.transfer(fis, fos, part_length, bytesTransfered);
				} else
					FileHelper.transfer(fis, fos, last_part_length, bytesTransfered);

				fos.close();

				i++;
			}

			fis.close();

		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	public File getInputFile() {
		return f;
	}

	protected void setFile(File f) {
		this.f = f;
	}

	public long getMaxSize() {
		return maxSize;
	}

	protected void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

	public int getBufferLength() {
		return bufferLength;
	}

	public void setBufferLength(int bufferLength) {
		this.bufferLength = bufferLength;
	}

	public File getDestinationDirectory() {
		return destinationDirectory;
	}

	@Override
	public void start() {
		split();
	}

	@Override
	public double getPercentage() {
		double result = ((double) bytesTransfered[0] / (getInputFile().length())) * 100;
		if (Double.isNaN(result)) return 0;
		return result;
	}

	@Override
	public String checkForErrors() {
		String errors = "";
		if (getInputFile() == null)
			errors = errors.concat("File not specified").concat(System.lineSeparator());
		else if (!getInputFile().exists()) {
			errors = errors.concat("File not found").concat(System.lineSeparator());
		}
		if (getBufferLength() <= 0)
			errors = errors.concat("Invalid buffer length").concat(System.lineSeparator());
		if (getMaxSize() <= 0)
			errors = errors.concat("Invalid part size length").concat(System.lineSeparator());
		if (!getDestinationDirectory().isDirectory()) {
			errors = errors.concat("Invalid destination path").concat(System.lineSeparator());
		}

		return errors;
	}

	@Override
	public File getOutputFile() {
		long parts = (f.length() + maxSize - 1) / maxSize;// formula to round up an integer division (positive only
		// members)
		long file_length = f.length();
		int extension_length = String.valueOf(parts - 1).length();
		if (extension_length < 3)
			extension_length = 3;
		return new File(destinationDirectory.getAbsolutePath() + File.separator + f.getName() + "."
				+ String.format("%0" + extension_length + "d", 1));
	}

}
