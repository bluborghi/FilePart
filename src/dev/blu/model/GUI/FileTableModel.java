package dev.blu.model.GUI;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import dev.blu.model.core.FileConfiguration;
import dev.blu.model.enums.ProcessStatus;

/**
 * Custom {@link TableModel} to show Name, status and percentage of a {@link FileConfiguration}
 * @author blubo
 *
 */
public class FileTableModel extends AbstractTableModel {
	private Vector<FileConfiguration> configs;
	private String[] cols = { "Name", "Status", "%" };

	/**
	 * Initializes the {@link FileTableModel}
	 * @param configs {@link Vector} of {@link FileConfiguration} 
	 */
	public FileTableModel(Vector<FileConfiguration> configs) {
		this.configs = configs;
	}

	@Override
	public int getRowCount() {
		return configs.size();
	}

	@Override
	public int getColumnCount() {
		return cols.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return configs.get(rowIndex).getFile().getName();
		}
		if (columnIndex == 1) {
			return configs.get(rowIndex).getState().toString();
		}
		if (columnIndex == 2) {
			if (configs.get(rowIndex).getState() == ProcessStatus.Running) {
				if (configs.get(rowIndex).getPercentage() != -1) {
					return configs.get(rowIndex).getPercentage() + "%";
				}
			} else if (configs.get(rowIndex).getState() == ProcessStatus.Completed) {
				return 100 + "%";
			}
			return "";
		}
		return null;
	}

	/**
	 * Gets the column name
	 * @param col_index The index of the column
	 * @return The name of the column
	 */
	public String getColumnName(int col_index) {
		return cols[col_index].toString();
	}
	
	/**
	 * Fires an event that updates the table after one insertion at the specified index
	 * @param index The index where the insertion occurred
	 */
	public void fireTableRowsInsertedAt(int index) { //after configs.add(c)
		fireTableRowsInserted(index, index);
	}
	
	/**
	 * Fires an event that updates the table after one deletion at the specified index
	 * @param index The index where the deletion occurred
	 */
	public void fireTableRowsDeletedAt(int index) { //after configs.remove()
		fireTableRowsDeleted(index, index);
	}
	
	/**
	 * Fires an event that updates the table after one row update at the specified index
	 * @param index The index where the row update occurred
	 */
	public void fireTableRowsUpdatedAt(int index) { //after config.setPercentage(value) or config.setState(value)
		fireTableRowsUpdated(index, index);		
	}
	
}
