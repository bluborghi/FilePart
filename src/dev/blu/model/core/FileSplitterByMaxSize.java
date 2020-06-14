package dev.blu.model.core;

import java.io.*;

import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.helpers.FileHelper;
import dev.blu.model.interfaces.FileAction;

/**
 * Splits a file in parts with a maximum size for each part
 * @author blubo
 *
 */
public class FileSplitterByMaxSize implements FileAction {
	protected File f;
	protected long maxSize;
	protected int bufferLength;
	protected long[] bytesTransfered = new long[1];
	protected File outputDir;
	protected final static int DEF_BUF_LEN = 1 * 1024 * 1024; // 1 MiB
	private boolean[] stop = {false};//returns stop as an array (pointer workaround)
	private ProcessStatus status;
	
	private FileSplitterByMaxSize(File f, long maxSize, int bufferLength, String dest) {
		setInputFile(f);
		setMaxSize(maxSize);
		setBufferLength(bufferLength);
		bytesTransfered[0] = 0;

		if (dest == null || dest.isEmpty()) {
			if (f != null && f.getParentFile().isDirectory()) {
				dest = f.getParent();
			}
		}
		outputDir = new File(dest);
		status = ProcessStatus.Ready;
	}

	private FileSplitterByMaxSize(File f, long maxSize, String dest) {
		this(f, maxSize, DEF_BUF_LEN, dest); // 1MiB standard buffer
	}

	/**
	 * Initializes {@link FileSplitterByMaxSize} using a {@link FileConfiguration}
	 * @param conf The {@link FileConfiguration}
	 */
	public FileSplitterByMaxSize(FileConfiguration config) {
		this(config.getFile(),
				config.getActionConfig().getPartSize() * config.getActionConfig().getUnit().getMultiplier(),
				config.getActionConfig().getOutputDir());
	}

	/**
	 * Initializes {@link FileSplitterByMaxSize} using an input {@link File} and a {@link FileActionConfiguration}
	 * @param inputFile The {@link File} to split
	 * @param params The {@link FileActionConfiguration} parameters
	 */
	public FileSplitterByMaxSize(File inputFile, FileActionConfiguration params) {
		this(inputFile, params.getPartSize() * params.getUnit().getMultiplier(), params.getOutputDir());
	}

	/**
	 * Split the input file with the given parameters
	 * @return
	 */
	public void split() {
		if (maxSize <= 0) {
			if (this instanceof FileSplitterByPartNumber) {
				FileSplitterByPartNumber splitter = (FileSplitterByPartNumber) this;
				maxSize = splitter.calcMaxSize();
			}
			else {
				return;	
			}
		}
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
				File outputFile = new File(outputDir.getAbsolutePath() + File.separator + f.getName() + "."
						+ String.format("%0" + extension_length + "d", i));
				FileOutputStream fos = null;
				fos = new FileOutputStream(outputFile);

				if (i < parts) {
					FileHelper.transfer(fis, fos, part_length, bytesTransfered, stop);
				} else
					FileHelper.transfer(fis, fos, last_part_length, bytesTransfered, stop);

				fos.close();

				i++;
			}

			fis.close();
			
			if (stop[0]) {
				i = 1;
				while (i <= parts) {
					File fileToDelete = new File(outputDir.getAbsolutePath() + File.separator + f.getName() + "."
							+ String.format("%0" + extension_length + "d", i));
					fileToDelete.delete();
					i++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Gets the input {@link File}
	 * @return The input {@link File}
	 */
	public File getInputFile() {
		return f;
	}

	protected void setInputFile(File f) {
		this.f = f;
	}

	/**
	 * Gets the maximum size of file output partition
	 * @return The max partition size
	 */
	public long getMaxSize() {
		return maxSize;
	}

	protected void setMaxSize(long maxSize) {
		if (this.maxSize > 0 && maxSize == 0)
			return;
		this.maxSize = maxSize;
	}

	/**
	 * Gets the buffer length
	 * @return The buffer length
	 */
	public int getBufferLength() {
		return bufferLength;
	}

	/**
	 * Sets the buffer length
	 * @param bufferLength The buffer length
	 */
	public void setBufferLength(int bufferLength) {
		this.bufferLength = bufferLength;
	}

	/**
	 * Gets the output files directory
	 * @return the output directory as a {@link File}
	 */
	public File getOutputDir() {
		return outputDir;
	}

	@Override
	public void start() {
		status = ProcessStatus.Running;
		split();
		if (stop[0]) {
			status = ProcessStatus.Stopped;
		}
		else {
			status = ProcessStatus.Completed;
		}
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
		if (!getOutputDir().isDirectory()) {
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
		return new File(outputDir.getAbsolutePath() + File.separator + f.getName() + "."
				+ String.format("%0" + extension_length + "d", 1));
	}

	@Override
	public void clear() {
		//nothing to clear
	}

	private void setStop(boolean stop) { 
		this.stop[0] = stop;
	}

	@Override
	public void stopAction() {
		status = ProcessStatus.Stopping;
		setStop(true);
	}

	@Override
	public ProcessStatus getActionStatus() {
		return status;
	}

}
