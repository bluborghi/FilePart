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
import dev.blu.model.enums.SplitOption;
import dev.blu.model.interfaces.FileAction;
import dev.blu.model.output.FileTableModel;

public class AppModel {
	FileTableModel tableModel;

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

	public void updateConfig(UUID id, SplitConfiguration splitConfig) {
		tableModel.getConfig(id).setSplitConfig(splitConfig);
	}
	
	public Vector<FileActionThread> prepareThreads() {
		Vector<FileActionThread> threads = new Vector<FileActionThread>(0, 5);
		for (FileConfiguration conf : tableModel.getConfigs()) {
			String err = "";
			if (conf.getState() == ProcessStatus.Ready) {
				SplitConfiguration params = conf.getSplitConfig();
				FileAction action = null;
				
				if (params == null) {
					err = err.concat(conf.getFile().getName() + " ignored: ").concat(System.lineSeparator());
					err = err.concat("No configuration given").concat(System.lineSeparator());
				}
				else if (params.getPw() != null && params.getPw().length>0) {
					if (params.getPartSize()>0 || params.getPartNumber()>0)
						action = new FileEncryptAndSplit(conf);
					else // no split params => merge
						action = new FileMergeAndDecrypt(conf);
				}
				else { //no password => no encryption
					if (params.getPartSize()>0)
						action = new FileSplitterByMaxSize(conf);
					if (params.getPartNumber()>0)
						action = new FileSplitterByPartNumber(conf);
					else // no split params => merge
						action = new FileMerger(conf);
				}
				
				if (action != null) {
					String actionError = action.checkForErrors();
					if (actionError != null && !actionError.isEmpty()){
						err = err.concat(conf.getFile().getName() + " ignored: ").concat(System.lineSeparator());
						err = err.concat(actionError);
					}
				}
				
				threads.add(new FileActionThread(action,err));
			}
			
			
		}
		
		return threads;
	}
	
	public Vector<FileActionThread> startThreads(Vector<FileActionThread> allThreads){
		Vector<FileActionThread> startedThreads = new Vector<FileActionThread>(0,5);
		for (FileActionThread t : allThreads) {
			if (!t.hasErrors()) {
				t.start();
				startedThreads.add(t);				
			}
		}
		return startedThreads;
	}
}
