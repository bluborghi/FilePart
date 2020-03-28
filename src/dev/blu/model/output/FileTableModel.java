package dev.blu.model.output;

import java.io.File;
import java.util.UUID;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import dev.blu.model.core.FileConfiguration;
import dev.blu.model.enums.ProcessStatus;

public class FileTableModel extends AbstractTableModel {
	private Vector<FileConfiguration> configs;
	private String[] cols = {"Name","Status","%"};
	
	public FileTableModel() {
		configs = new Vector<FileConfiguration>(0,5);
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
			} else
			if (configs.get(rowIndex).getState() == ProcessStatus.Completed) {	
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

	public UUID addFile(File file, UUID id) {
		FileConfiguration c = new FileConfiguration(file, id);
		configs.add(c);
		fireTableRowsInserted(getRowCount(),getRowCount());
		return id;
	}
	
	

	public int removeFile(UUID id) {
		int index = getIndex(id);
		return removeFileAt(index);
	}
	
	public int removeFileAt(int index) {
		try {
			configs.remove(index);
			fireTableRowsDeleted(index, index);
		} catch (ArrayIndexOutOfBoundsException e) {
			return 1;
		}
		return 0;
	}
	
	
	
	public FileConfiguration getConfig(int index) {
		return configs.get(index);
	}
	
	
	public int getIndex(UUID id) {
		int i = 0;
		for (FileConfiguration fc : configs) {
			if (fc.getId().equals(id)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	
	
	
	
	
	/* OLD DEPRECATED STUFF
	public void setProcessStatus(UUID id, ProcessStatus ps) {
		int index = configIndexOf(id);
		setProcessStatus(index, ps);
	}
	
	public void setProcessStatus(int index, ProcessStatus ps) {
		try {
			configs.get(index).setState(ps);
			fireTableRowsUpdated(index, index);
		} catch (IndexOutOfBoundsException e) {
			System.err.println("cant set index "+index+" because out of bounds (maybe exceeded states.size() = "+states.size()+")");
		}
	}
	
	public UUID getId(int index) {
		try {
			return configs.get(index);			
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public File getFile(int index) {
		return files.get(index);
	}
	
	public File getFile(UUID id) {
		int index = ids.indexOf(id);
		return getFile(index);
	}
	
	public int getIndex(UUID id) {
		return ids.indexOf(id);
	}
	
	public ProcessStatus getProcessStatus(int index) {
		return states.get(index);
	}

	

	public void setPercentage(UUID id, double percentage) {
		int index = getIndex(id);
		//System.out.println("setting "+ percentages.get(index)+ " at index " + index + " => " + Math.round(percentage));
		percentages.set(index, (int) Math.round(percentage));
		fireTableRowsUpdated(index, index);
	} */

	
}
