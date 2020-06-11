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
					if (params.getPartNumber() > 0)
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
				preparedThreads.add(new FileActionThread(action, err));
			}

		}
		return preparedThreads;
	}

	public Vector<FileActionThread> startThreads() {
		if (preparedThreads == null || preparedConfigs == null)
			return null;
		
		startedThreads = new Vector<FileActionThread>(0, 5);
		int i = 0;
		for (FileActionThread t : preparedThreads) {
			if (!t.hasErrors()) {
				t.start();
				new ProgressThread(i, t).start();
				startedThreads.add(t);
			}
			i++;
		}

		preparedThreads = null;
		return startedThreads;
	}
	
	public void stopThreads() {
		startedThreads.forEach((t) -> {
			t.stopAction();
		});
	}

	private class ProgressThread extends Thread {
		private int index;
		private FileActionThread t;

		public ProgressThread(int index, FileActionThread t) {
			this.index = index;
			this.t = t;
		}
		
		@Override
		public void run() {
			if (t.isAlive())
				setState(index,ProcessStatus.Running);
			
			while(t.isAlive()) {
				double perc = t.getPercentage();
				setPercentage(index, perc);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setState(index,ProcessStatus.Completed);
		}
	}
	
	protected void setPercentage(int index, double perc) {
		getFileConfigAt(index).setPercentage(perc);
	}
	
	protected void setState(int index, ProcessStatus state) {
		getFileConfigAt(index).setState(state);
	}

}
