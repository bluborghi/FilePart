package dev.blu.model.GUI;

import java.io.File;
import java.io.ObjectInputFilter.Config;
import java.util.UUID;
import java.util.Vector;

import javax.swing.table.TableModel;

import dev.blu.model.AppModel;
import dev.blu.model.GUI.enums.ActionType;
import dev.blu.model.core.FileConfiguration;
import dev.blu.model.core.FileActionConfiguration;
import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.helpers.FileHelper;

/**
 * Holds the GUI related data, such as the {@link FileTableModel}
 * @author blubo
 *
 */
public class GUIModel extends AppModel {
	private FileTableModel ftm;
	private Vector<ActionType> actionTypes;
	
	/**
	 * Initializes the {@link GUIModel} calling the constructor of {@link AppModel} adding GUI related data.
	 */
	public GUIModel() {
		super();
		ftm = new FileTableModel(super.getFileConfigs());
		actionTypes = new Vector<ActionType>(0,5);
	}
	
	/**
	 * Gets the tableModel
	 * @return The {@link FileTableModel}
	 */
	public TableModel getTableModel() {
		return ftm;
	}
	
	/**
	 * Gets the action types {@link Vector}
	 * @return The actionTypes {@link Vector}
	 */
	public Vector<ActionType> getActionTypes() {
		return actionTypes;
	}
	
	/**
	 * Gets the {@link ActionType} at a given index
	 * @param index The index of the element to retrieve
	 * @return the {@link ActionType} requested, null the index is invalid
	 */
	public ActionType getActionTypeAt(int index) {
		if (index<0 || actionTypes==null || index>=actionTypes.size()) 
			return null;
		return actionTypes.get(index);
	}
	
	/**
	 * Sets the {@link ActionType} at a given index
	 * @param index The index of the element to set
	 * @param at The new {@link ActionType} value
	 */
	public void setActionTypeAt(int index, ActionType at) {
		actionTypes.set(index, at);
	}

	@Override
	public File removeFileAt(int index) {
		File removed = super.removeFileAt(index);
		actionTypes.removeElementAt(index);
		ftm.fireTableRowsDeletedAt(index);
		return removed;
	}
	
	@Override
	public UUID addFile(File file) {
		UUID newFileId = super.addFile(file);
		addActionType(getActionTypeByFile(file));
		ftm.fireTableRowsInsertedAt(super.getConfigsCount());
		return newFileId;
	}
	
	@Override
	public void updateConfig(UUID id, FileActionConfiguration splitConfig) {
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
	
	private void addActionType(ActionType at) {
		actionTypes.add(at);
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
