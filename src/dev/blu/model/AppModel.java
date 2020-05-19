package dev.blu.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import dev.blu.model.core.FileActionThread;
import dev.blu.model.core.FileConfiguration;
import dev.blu.model.core.FileDecryptor;
import dev.blu.model.core.FileEncryptAndSplit;
import dev.blu.model.core.FileEncryptor;
import dev.blu.model.core.FileMergeAndDecrypt;
import dev.blu.model.core.FileMerger;
import dev.blu.model.core.FileSplitterByMaxSize;
import dev.blu.model.core.FileSplitterByPartNumber;
import dev.blu.model.core.SplitConfiguration;
import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.interfaces.FileAction;
import dev.blu.model.output.FileTableModel;

public class AppModel {
	FileTableModel tableModel;
	Vector<FileActionThread> preparedThreads;
	Vector<FileConfiguration> preparedConfigs;

	public AppModel() {
		tableModel = new FileTableModel();
	}

	public UUID addFile(File file) {
		UUID id = UUID.randomUUID();
		tableModel.addFile(file, id);
		return id;
	}

	public File removeFileAt(int index) {
		File f = tableModel.getConfig(index).getFile();
		if (tableModel.removeFileAt(index) == 0) {
			return f; // return removed file
		}
		return null; // if no file was found
	}

	public File removeFile(UUID id) {
		return removeFileAt(tableModel.getIndex(id));
	}

	public FileTableModel getTableModel() {
		return tableModel;
	}
	
	public int getConfigsCount() {
		return getTableModel().getConfigs().size();
	}

	public void updateConfig(UUID id, SplitConfiguration splitConfig) {
		tableModel.getConfig(id).setSplitConfig(splitConfig);
	}

	public SplitConfiguration getConfig(UUID id) {
		return tableModel.getConfig(id).getSplitConfig();
	}

	public Vector<FileActionThread> prepareThreads() {
		preparedThreads = new Vector<FileActionThread>(0, 5);
		preparedConfigs = new Vector<FileConfiguration>(0, 5);
		for (FileConfiguration conf : tableModel.getConfigs()) {
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
		
		Vector<FileActionThread> startedThreads = new Vector<FileActionThread>(0, 5);
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
				tableModel.setState(index, ProcessStatus.Running);
			
			while(t.isAlive()) {
				double perc = t.getPercentage();
				tableModel.setPercentage(index, perc);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			tableModel.setState(index, ProcessStatus.Completed);
		}
	}
}
