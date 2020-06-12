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
import dev.blu.model.core.SplitConfiguration;
import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.interfaces.FileAction;

public class AppModel {
	Vector<FileConfiguration> configs;
	Vector<FileActionThread> preparedThreads;
	Vector<FileConfiguration> preparedConfigs;
	Vector<FileActionThread> startedThreads;
	boolean isRunning = false;

	public AppModel() {
		configs = new Vector<FileConfiguration>(0,5);
	}
	
	protected Vector<FileConfiguration> getFileConfigs() {
		return configs;
	}

	public UUID addFile(File file) {
		UUID id = UUID.randomUUID();
		FileConfiguration c = new FileConfiguration(file, id);
		configs.add(c);		
		return id;
	}

	public File removeFileAt(int index) {
		File f = getFileConfigAt(index).getFile();
		
		try {
			configs.remove(index);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;  // if no file was found
		}
		return f;  // returns removed file
	}

	public FileConfiguration getFileConfigAt(int index) {
		if (index < 0 || configs==null || index >= configs.size()) return null;
		return configs.get(index);	
	}
	
	public FileConfiguration getFileConfig(UUID id) {
		return getFileConfigAt(getFileConfigIndex(id));
	}

	public File removeFile(UUID id) {
		return removeFileAt(getFileConfigIndex(id));
	}
	
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
	
	public int getConfigsCount() {
		return configs.size();
	}

	public void updateConfig(UUID id, SplitConfiguration splitConfig) {
		getFileConfig(id).setSplitConfig(splitConfig);
	}

	public SplitConfiguration getConfig(UUID id) {
		return getFileConfig(id).getSplitConfig();
	}

	public Vector<FileActionThread> prepareThreads() {
		preparedThreads = new Vector<FileActionThread>(0, 5);
		preparedConfigs = new Vector<FileConfiguration>(0, 5);
		for (FileConfiguration conf : configs) {
			String err = "";
			if (conf.getState() == ProcessStatus.Ready) {
				SplitConfiguration params = conf.getSplitConfig();
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
