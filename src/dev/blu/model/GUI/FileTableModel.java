package dev.blu.model.GUI;

import java.io.File;
import java.util.UUID;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import dev.blu.model.core.FileConfiguration;
import dev.blu.model.enums.ProcessStatus;

public class FileTableModel extends AbstractTableModel {
	private Vector<FileConfiguration> configs;
	private String[] cols = { "Name", "Status", "%" };

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

//	public void setValueAt(Object value, int row, int col) {
//        data[row][col] = value;
//        fireTableCellUpdated(row, col);
//    }

	public String getColumnName(int col_index) {
		return cols[col_index].toString();
	}

//	public UUID addFile(File file, UUID id) {
//		FileConfiguration c = new FileConfiguration(file, id);
//		configs.add(c);
//		fireTableRowsInserted(getRowCount(), getRowCount());
//		return id;
//	}
	
	public void fireTableRowsInsertedAt(int index) { //after configs.add(c)
		fireTableRowsInserted(index, index);
	}
	
	public void fireTableRowsDeletedAt(int index) { //after configs.remove()
		fireTableRowsDeleted(index, index);
	}
	
	public void fireTableRowsUpdatedAt(int index) { //after config.setPercentage(value) or config.setState(value)
		fireTableRowsUpdated(index, index);		
	}
	

//	public int removeFile(UUID id) {
//		int index = getIndex(id);
//		return removeFileAt(index);
//	}
//
//	public int removeFileAt(int index) {
//		try {
//			configs.remove(index);
//			fireTableRowsDeleted(index, index);
//		} catch (ArrayIndexOutOfBoundsException e) {
//			return 1;
//		}
//		return 0;
//	}

//	public FileConfiguration getConfig(int index) {
//		if (index < 0 || index >= configs.size()) return null;
//		return configs.get(index);
//	}

//	public FileConfiguration getConfig(UUID id) {
//		return getConfig(getIndex(id));
//	}

//	public int getIndex(UUID id) {
//		int i = 0;
//		for (FileConfiguration fc : configs) {
//			if (fc.getId().equals(id)) {
//				return i;
//			}
//			i++;
//		}
//		return -1;
//	}

//	public Vector<FileConfiguration> getConfigs() {
//		return configs;
//	}

//	public void setPercentage(int index, double percentage) {
//		FileConfiguration fc = getConfig(index);
//		if (fc != null) {
//			System.out.println("setting percentage: "+percentage+" at index "+ index);
//			fc.setPercentage(percentage);
//			this.fireTableRowsUpdated(index, index);
//		}
//	}

//	public void setState(int index, ProcessStatus state) {
//		FileConfiguration fc = getConfig(index);
//		if (fc != null) {
//			fc.setState(state);
//			this.fireTableRowsUpdated(index, index);	
//		}
//	}

}
