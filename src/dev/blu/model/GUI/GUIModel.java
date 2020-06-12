package dev.blu.model.GUI;

import java.io.File;
import java.io.ObjectInputFilter.Config;
import java.util.UUID;
import java.util.Vector;

import javax.swing.table.TableModel;

import dev.blu.model.AppModel;
import dev.blu.model.GUI.enums.ActionType;
import dev.blu.model.core.FileConfiguration;
import dev.blu.model.core.SplitConfiguration;
import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.helpers.FileHelper;

public class GUIModel extends AppModel {
	private FileTableModel ftm;
	private Vector<DetailsPanelOptions> sidePanels;
	
	
	public GUIModel() {
		super();
		ftm = new FileTableModel(super.getFileConfigs());
		sidePanels = new Vector<DetailsPanelOptions>(0,5);
	}
	
	public TableModel getTableModel() {
		return ftm;
	}
	
	public Vector<DetailsPanelOptions> getSidePanels() {
		return sidePanels;
	}
	
	public DetailsPanelOptions getSidePanelAt(int index) {
		if (index<0 || sidePanels==null || index>=sidePanels.size()) 
			return null;
		return sidePanels.get(index);
	}

	@Override
	public File removeFileAt(int index) {
		File removed = super.removeFileAt(index);
		sidePanels.removeElementAt(index);
		ftm.fireTableRowsDeletedAt(index);
		return removed;
	}
	
	@Override
	public UUID addFile(File file) {
		UUID newFileId = super.addFile(file);
		addSidePanel(getActionTypeByFile(file));
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
	protected void setPercentage(UUID id, double perc) {
		super.setPercentage(id, perc);
		ftm.fireTableRowsUpdatedAt(getFileConfigIndex(id));
	}
	
	@Override
	protected void setState(UUID id, ProcessStatus state) {
		super.setState(id, state);
		ftm.fireTableRowsUpdatedAt(super.getFileConfigIndex(id));
	}
	
	private void addSidePanel(ActionType at) {
		DetailsPanelOptions dpo = new DetailsPanelOptions(at);
		sidePanels.add(dpo);
	}
	
	private ActionType getActionTypeByFile(File f) {		
		String ext = FileHelper.getFileExtension(f.getName());
		if (ext.matches("\\d+")) { // es: 001
			return ActionType.Merge;
		} else {
			return ActionType.SplitByMaxSize;
		}
	}
}
