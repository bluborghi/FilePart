package dev.blu.model;

import java.io.File;
import java.util.UUID;

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
			return f; //return removed file
		}
		return null; //if no file was found
	}

	public File removeFile(UUID id) {
		return removeFileAt(tableModel.getIndex(id));
	}
	
}
