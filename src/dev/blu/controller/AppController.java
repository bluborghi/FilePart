package dev.blu.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import dev.blu.model.FileActionThread;
import dev.blu.model.FileMerger;
import dev.blu.model.FileSplitterByMaxSize;
import dev.blu.model.FileSplitterByPartNumber;
import dev.blu.model.ProcessStatus;
import dev.blu.model.SplitConfiguration;
import dev.blu.view.AppView;

public class AppController {
	private AppView view;

	public AppController(AppView view) {
		setView(view);

		view.setAddButtonActionListener(new AddButtonActionListener());
		view.setRemoveButtonActionListener(new RemoveButtonActionListener());
		view.setStartButtonActionListener(new StartButtonActionListener());
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

	class StartButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			AppView view = getView();
			Vector<SplitConfiguration> queue = view.getQueue();
			HashMap<UUID,FileActionThread> threads = new HashMap<UUID,FileActionThread>();
			
			for (SplitConfiguration c : queue) {
				
				switch (c.getSplitOption()) {
				case DoNothing: {
					break;
				}
				case Merge: {
					File f = view.getFile(c.getId());
					FileActionThread t = new FileActionThread(new FileMerger(f));
					threads.put(c.getId(), t);
					break;
				}
				case MergeAndDecrypt: {
					break;
				}
				case SplitAndEncrypt: {
					break;
				}
				case SplitByMaxSize: {
					File f = view.getFile(c.getId());
					FileActionThread t = new FileActionThread(new FileSplitterByMaxSize(f,c.getPartSize()));
					threads.put(c.getId(), t);
					break;
				}
				case SplitByPartNumber: {
					File f = view.getFile(c.getId());
					FileActionThread t = new FileActionThread(new FileSplitterByPartNumber(f,c.getPartNumber()));
					threads.put(c.getId(), t);
					break;
				}
				}
				
			}
			
			threads.forEach((id,t) -> {
				t.start();
				view.setProcessStatus(id, ProcessStatus.Running);
			});
			
			new ProgressThread(threads).start();
			view.setEnabledStartButton(false);
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

	class ProgressThread extends Thread {
		HashMap<UUID,FileActionThread> threads;
		
		public ProgressThread(HashMap<UUID,FileActionThread> threads) {
			this.threads = threads;
		}
		
		@Override
		public void run() {
			boolean someoneAlive = true;
			while (someoneAlive) {
				someoneAlive = false;
				for(Map.Entry<UUID, FileActionThread> entry : threads.entrySet()) {
				    FileActionThread t = entry.getValue();
				    UUID id = entry.getKey();
				    if (t.isAlive()) {
				    	someoneAlive = true;
				    	view.setPercentage(id,t.getPercentage());
				    }
				    else {
				    	view.setProcessStatus(id, ProcessStatus.Completed);
				    }
				}
			}
			view.setEnabledStartButton(true);
		}
	}
}
