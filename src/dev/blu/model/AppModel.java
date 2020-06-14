package dev.blu.model;

import java.io.File;
import java.util.UUID;
import java.util.Vector;

import dev.blu.model.core.FileActionThread;
import dev.blu.model.core.FileConfiguration;
import dev.blu.model.core.FileEncryptAndSplit;
import dev.blu.model.core.FileMergeAndDecrypt;
import dev.blu.model.core.FileMerger;
import dev.blu.model.core.FileSplitterByMaxSize;
import dev.blu.model.core.FileSplitterByPartNumber;
import dev.blu.model.core.FileActionConfiguration;
import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.interfaces.FileAction;

public class AppModel {
	private Vector<FileConfiguration> configs;
	private Vector<FileActionThread> preparedThreads;
	private Vector<FileConfiguration> preparedConfigs;
	private Vector<FileActionThread> startedThreads;
	private boolean isRunning = false;

	/**
	 * Initializes the {@link AppModel}
	 */
	public AppModel() {
		configs = new Vector<FileConfiguration>(0,5);
	}
	
	protected Vector<FileConfiguration> getFileConfigs() {
		return configs;
	}

	/**
	 * Creates a {@link FileConfiguration} from the given file and adds it to {@link AppModel}'s internal list
	 * @param file The file to add
	 * @return The {@link FileConfiguration} ID
	 */
	public UUID addFile(File file) {
		FileConfiguration c = new FileConfiguration(file);
		configs.add(c);		
		return c.getId(); 
    }

	/**
	 * Removes a {@link FileConfiguration} at the specified index
	 * @param index The specified index
	 * @return The removed {@link File}
	 */
	public File removeFileAt(int index) {
	
		File f = getFileConfigAt(index).getFile();
		
		try {
			configs.remove(index);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;  // if no file was found
		}
		return f;  // returns removed file
	}

	/**
	 * Gets the {@link FileConfiguration} at a given index
	 * @param index The given index
	 * @return The requested {@link FileConfiguration}, <code>null</code> if the index is invalid
	 */
	public FileConfiguration getFileConfigAt(int index) {
		if (index < 0 || configs==null || index >= configs.size()) return null;
		return configs.get(index);	
	}
	
	/**
	 * Gets the {@link FileConfiguration} with the given ID
	 * @param id The given {@link UUID}
	 * @return The requested {@link FileConfiguration}
	 */
	public FileConfiguration getFileConfig(UUID id) {
		return getFileConfigAt(getFileConfigIndex(id));
	}

	/**
	 * Removes a {@link FileConfiguration} with the specified ID
	 * @param id The specified {@link UUID}
	 * @return The removed {@link File}
	 */
	public File removeFile(UUID id) {
		return removeFileAt(getFileConfigIndex(id));
	}
	
	/**
	 * Gets the index of the {@link FileConfiguration} with the specified ID
	 * @param id The specified {@link UUID}
	 * @return the corresponding index
	 */
	public int getFileConfigIndex(UUID id) {
		int i = 0;
		for (FileConfiguration fc : configs) {
			if (fc.getId().equals(id)) {
				return i;
			}
			i++;
		}
		return -1;
	}	
	
	/**
	 * Gets the current number of {@link FileConfiguration}s stored
	 * @return The number of file configurations
	 */
	public int getConfigsCount() {
		return configs.size();
	}

	/**
	 * Updates the {@link FileActionConfiguration} of the matching {@link FileConfiguration}
	 * @param id The {@link UUID} of the {@link FileConfiguration} to update
	 * @param fileActionConfig The new {@link FileActionConfiguration}
	 */
	public void updateConfig(UUID id, FileActionConfiguration fileActionConfig) {
		getFileConfig(id).setActionConfig(fileActionConfig);
	}

	/**
	 * Gets the {@link FileActionConfiguration} of the given ID
	 * @param id The given {@link UUID}
	 * @return The requested {@link FileActionConfiguration}
	 */
	public FileActionConfiguration getConfig(UUID id) {
		return getFileConfig(id).getActionConfig();
	}

	/**
	 * Prepares the thread to be executed, checking for errors and filtering the state
	 * @return a collection of the prepared threads
	 */
	public Vector<FileActionThread> prepareThreads() {
		preparedThreads = new Vector<FileActionThread>(0, 5);
		preparedConfigs = new Vector<FileConfiguration>(0, 5);
		for (FileConfiguration conf : configs) {
			String err = "";
			if (conf.getState() == ProcessStatus.Ready) {
				FileActionConfiguration params = conf.getActionConfig();
				FileAction action = null;

				if (params == null) {
					err = err.concat(conf.getFile().getName() + " ignored: ").concat(System.lineSeparator());
					err = err.concat("No configuration given").concat(System.lineSeparator());
				} else if (params.getPw() != null && params.getPw().length > 0) {
					if (params.getPartSize() > 0 || params.getPartNumber() > 0)
						action = new FileEncryptAndSplit(conf);
					else // no split params => merge
						action = new FileMergeAndDecrypt(conf);
				} else { // no password => no encryption
					if (params.getPartSize() > 0)
						action = new FileSplitterByMaxSize(conf);
					else if (params.getPartNumber() > 0)
						action = new FileSplitterByPartNumber(conf);
					else // no split params => merge
						action = new FileMerger(conf);
				}

				if (action != null) {
					String actionError = action.checkForErrors();
					if (actionError != null && !actionError.isEmpty()) {
						err = err.concat(conf.getFile().getName() + " ignored: ").concat(System.lineSeparator());
						err = err.concat(actionError);
						// conf.setState(ProcessStatus.Error);
					}
				}

				preparedConfigs.add(conf);
				preparedThreads.add(new FileActionThread(action, err, conf.getId()));
			}

		}
		return preparedThreads;
	}

	/**
	 * Start the previously prepared threads
	 * @return the started threads
	 */
	public Vector<FileActionThread> startThreads() {
		if (preparedThreads == null || preparedConfigs == null)
			return null;
		
		startedThreads = new Vector<FileActionThread>(0, 5);
		
		for (FileActionThread t : preparedThreads) {
			if (!t.hasErrors()) {
				t.start();
				
				new ProgressThread(t).start();
				startedThreads.add(t);
			}
		}

		preparedThreads = null;
		new OverallProgressThread().start();
		return startedThreads;
	}
	
	/**
	 * Stop all running {@link FileAction}s
	 */
	public void stopThreads() {
		startedThreads.forEach((t) -> {
			t.stopAction();
		});
	}
	
	private class ProgressThread extends Thread {
		private FileActionThread t;

		public ProgressThread(FileActionThread t) {
			this.t = t;
		}
		
		@Override
		public void run() {			
			while(t.isAlive()) {
				setState(t.getActionId(),t.getActionStatus());
				double perc = t.getPercentage();
				setPercentage(t.getActionId(), perc);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setState(t.getActionId(),t.getActionStatus());
		}
	}
	
	private class OverallProgressThread extends Thread {
		@Override
		public void run() {
			setIsRunning(true);
			boolean atLeastOneAlive = true;
			while (atLeastOneAlive) {
				atLeastOneAlive = false;
				for(Thread t : startedThreads) {
					if (t.isAlive())
						atLeastOneAlive = true;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setIsRunning(false);
		}

	}

	private void setIsRunning(boolean b) {
		isRunning = b;
	}
	
	/**
	 * Checks if there are {@link FileAction}s running
	 * @return <code>true</code> if there are {@link FileAction}s running, <code>false</code> otherwise
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	protected void setPercentage(UUID id, double perc) {
		getFileConfig(id).setPercentage(perc);
	}
	
	protected void setState(UUID id, ProcessStatus state) {
		getFileConfig(id).setState(state);
	}

}
