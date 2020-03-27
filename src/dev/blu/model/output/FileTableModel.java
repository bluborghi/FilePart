package dev.blu.model.output;

import java.io.File;
import java.util.UUID;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import dev.blu.model.enums.ProcessStatus;

public class FileTableModel extends AbstractTableModel {
	private Vector<File> files;
	private Vector<UUID> ids;
	private Vector<ProcessStatus> states;
	private Vector<Integer> percentages;
	private String[] cols = {"Name","Status","%"};
	
	public FileTableModel() {
		files = new Vector<File>(0,5);
		ids = new Vector<UUID>(0,5);
		states = new Vector<ProcessStatus>(0,5);
		percentages = new Vector<Integer>(0,5);
	}
	
	@Override
	public int getRowCount() {
		return files.size();
	}
	
	@Override
	public int getColumnCount() {
		return cols.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return files.get(rowIndex).getName();
		}
		if (columnIndex == 1) {
			return states.get(rowIndex).toString();
		}
		if (columnIndex == 2) {
			if (states.get(rowIndex) == ProcessStatus.Running) {
				if (percentages.get(rowIndex) != -1) {
					return percentages.get(rowIndex).toString() + "%";					
				}
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

	public void addFile(File file) {
		files.add(file);
		ids.add(UUID.randomUUID());
		states.add(ProcessStatus.Ready);
		percentages.add(-1);
		fireTableRowsInserted(getRowCount(),getRowCount());
	}

	public int removeFileAt(int index) {
		try {
			files.remove(index);
			ids.remove(index);
			fireTableRowsDeleted(index, index);
		} catch (ArrayIndexOutOfBoundsException e) {
			return 1;
		}
		return 0;
	}
	
	public void setProcessStatus(UUID id, ProcessStatus ps) {
		int index = ids.indexOf(id);
		setProcessStatus(index, ps);
	}
	
	public void setProcessStatus(int index, ProcessStatus ps) {
		try {
			if (index == states.size())
				throw new IndexOutOfBoundsException();
			
			states.set(index, ps); //also throws IndexOutOfBoundsException
			fireTableRowsUpdated(index, index);
		} catch (IndexOutOfBoundsException e) {
			System.err.println("cant set index "+index+" because out of bounds (maybe exceeded states.size() = "+states.size()+")");
		}
	}
	
	public UUID getId(int index) {
		try {
			return ids.get(index);			
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
	}
}
