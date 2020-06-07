package dev.blu.model.GUI;

import java.io.File;
import java.io.ObjectInputFilter.Config;
import java.util.UUID;

import javax.swing.table.TableModel;

import dev.blu.model.AppModel;
import dev.blu.model.core.SplitConfiguration;
import dev.blu.model.enums.ProcessStatus;

public class GUIModel extends AppModel {
	private FileTableModel ftm;
	
	public GUIModel() {
		super();
		ftm = new FileTableModel(super.getFileConfigs());
	}
	
	public TableModel getTableModel() {
		return ftm;
	}
	
	@Override
	public File removeFileAt(int index) {
		File removed = super.removeFileAt(index);
		ftm.fireTableRowsDeletedAt(index);
		return removed;
	}
	
	@Override
	public UUID addFile(File file) {
		UUID newFileId = super.addFile(file);
		ftm.fireTableRowsInsertedAt(super.getConfigsCount());
		return newFileId;
	}
	
	@Override
	public void updateConfig(UUID id, SplitConfiguration splitConfig) {
		super.updateConfig(id, splitConfig);
		int index = super.getFileConfigIndex(id);
		ftm.fireTableRowsUpdatedAt(index);
	}

	@Override
	protected void setPercentage(int index, double perc) {
		super.setPercentage(index, perc);
		ftm.fireTableRowsUpdatedAt(index);
	}
	
	@Override
	protected void setState(int index, ProcessStatus state) {
		super.setState(index, state);
		ftm.fireTableRowsUpdatedAt(index);
	}
	
}
