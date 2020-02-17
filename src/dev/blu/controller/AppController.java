package dev.blu.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.UUID;

import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.Element;

import dev.blu.view.AppView;

public class AppController {
	private AppView view;

	public AppController(AppView view) {
		setView(view);

		view.setAddButtonActionListener(new AddButtonActionListener());
		view.setRemoveButtonActionListener(new RemoveButtonActionListener());
		view.setFileListSelectionListener(new FileListSelectionListener());
		view.setFocusListener(new DetailsPanelFocusListener());
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
			// System.out.println("add action performed");
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
			// System.out.println("remove action performed");
			AppView view = getView();
			int file_index = view.getSelectedInedex();
			if (file_index != -1) {
				view.removeFile(file_index);
			}
		}
	}

	class FileListSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			AppView view = getView();
			File current = view.getSelectedFile();
			if (current != null) {
				view.showStatus("Current File: " + current.getPath());
				view.loadConfig();
			} else {
				view.showStatus("");
			}
		}
	}

	class DetailsPanelFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			
		}

		@Override
		public void focusLost(FocusEvent e) {
			view.saveConfig();
		}
		
	}

	

}
