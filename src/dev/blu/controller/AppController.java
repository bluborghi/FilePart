package dev.blu.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dev.blu.view.AppView;

public class AppController {
	private AppView view;
	
	
	public AppController(AppView view) {
		setView(view);

		view.setAddButtonActionListener(new AddButtonActionListener());
		view.setRemoveButtonActionListener(new RemoveButtonActionListener());
		//view.setFileListSelectionListener(new FileListSelectionListener());
	}

	public AppView getView() {
		return view;
	}

	public void setView(AppView view) {
		this.view = view;
	}
	
	class AddButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("add action performed");
			AppView view = getView();
			JFileChooser fc = view.getFileChooser();
			int returnVal = fc.showOpenDialog(view);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				System.out.println("Opening: " + file.getAbsolutePath());
				view.addFile(file);
			} else {
				System.out.println("Open command cancelled by user.");
			}
		}
	}
	
	class RemoveButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("remove action performed");
			AppView view = getView();
			File file = view.getSelectedFile();
			if (file != null) {
				view.removeFile(file);				
			}
		}
	}
	
	class FileListSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			AppView view = getView();
			File current = view.getSelectedFile();
			if (current != null) {
				view.showStatus("Current File: " + current.getName());	
				
				//WIP fill split options combo box
				
				
				//--------------------------------
			}
			else {
				view.showStatus("");	
			}
		}
	}
}
