package dev.blu.model;

import java.io.File;
import java.util.UUID;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {
	private Vector<File> files;
	private Vector<UUID> ids;
	private String[] cols = {"Name","Status"};
	
	public FileTableModel() {
		files = new Vector<File>(0,5);
		ids = new Vector<UUID>(0,5);
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
			return "WIP";
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
	
	public UUID getId(int index) {
		return ids.get(index);
	}

	public File getFile(int index) {
		return files.get(index);
	}
}
