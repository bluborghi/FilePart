package dev.blu.model;

import java.io.File;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {
	private Vector<File> files;
	private String[] cols = {"Name","Status"};
	
	public FileTableModel() {
		files = new Vector<File>();
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

	public void addFile(File file) {
		files.add(file);
	}

	public void removeFileAt(int index) {
		files.remove(index);
	}

}
