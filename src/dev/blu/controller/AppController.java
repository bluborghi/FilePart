package dev.blu.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JFileChooser;
import dev.blu.model.AppModel;
import dev.blu.model.core.FileActionThread;
import dev.blu.model.core.SplitConfiguration;
import dev.blu.model.enums.ByteUnit;
import dev.blu.view.AppView;

public class AppController {
	private AppView view;
	private AppModel model;

	public AppController(AppModel m, AppView v) {
		setView(v);
		setModel(m);
		
		view.setTableModel(model.getTableModel());
		
		view.addAddButtonActionListener(new AddButtonActionListener());
		view.addRemoveButtonActionListener(new RemoveButtonActionListener());
		view.addStartButtonActionListener(new StartButtonActionListener());
//		view.setFileListSelectionListener(new FileListSelectionListener());
//		view.setFocusListener(new DetailsPanelFocusListener());
//		view.addSplitOptionsItemListener(new SplitOptionItemListener());
		view.setVisible(true);
	}

	private void setView(AppView view) {
		this.view = view;
	}	

	private void setModel(AppModel model) {
		this.model = model;
	}

	class AddButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("add action performed");
			JFileChooser fc = view.getFileChooser();
			int returnVal = fc.showOpenDialog(view);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				//System.out.println("Opening: " + file.getAbsolutePath());
				UUID id = model.addFile(file);
				int index = model.getConfigsCount() -1;
				view.selectRows(index, index);
				//model.updateConfig(id, new SplitConfiguration(id, 3, 0, ByteUnit.B, "caccamelone".toCharArray(), "/run/media/blubo/Volume/FilePart/myFolder/mySecondOtherFolder"));
			} else {
				System.out.println("Open command cancelled by user.");
			}
		}
	}

	class RemoveButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// System.out.println("remove action performed");
			
			int file_index = view.getSelectedInedex();
			if (file_index != -1) {
				model.removeFileAt(file_index);
			}
		}
	}

	class StartButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {    	
	     	
			
			
	    	Vector<FileActionThread> threads = model.prepareThreads();
	    	
	    	for (FileActionThread t : threads) {
	    		if (t.hasErrors())
	    			System.err.print(t.getErrorMessage());
	    	}
	    	
	    	Vector<FileActionThread> startedThreads = model.startThreads();
	    	
	    	
//	    	boolean running = true;
//	    	while (running) {
//	    		running = false;
//	    		for (FileActionThread t : startedThreads) {
//	    			if (t.isAlive()) {
//	    				running = true;
//	    			}
//	    			System.out.print(t.getFile().getName() + ": " + Math.floor(t.getPercentage()*10)/10 + " | ");
//	    		}    		
//	    		System.out.println();
//	    		try {
//					Thread.sleep(100);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} 
//	    	}
//	    	
//	    	System.out.println("done");
			
			
//			Vector<SplitConfiguration> queue = view.getQueue();
//			HashMap<UUID,FileActionThread> threads = new HashMap<UUID,FileActionThread>();
//			
//			for (SplitConfiguration c : queue) {
//				
//				switch (c.getSplitOption()) {
//				case DoNothing: {
//					break;
//				}
//				case Merge: {
//					File f = view.getFile(c.getId());
//					FileActionThread t = new FileActionThread(new FileMerger(f));
//					threads.put(c.getId(), t);
//					break;
//				}
//				case MergeAndDecrypt: {
//					break;
//				}
//				case SplitAndEncrypt: {
//					break;
//				}
//				case SplitByMaxSize: {
//					File f = view.getFile(c.getId());
//					FileActionThread t = new FileActionThread(new FileSplitterByMaxSize(f,c.getPartSize()));
//					threads.put(c.getId(), t);
//					break;
//				}
//				case SplitByPartNumber: {
//					File f = view.getFile(c.getId());
//					FileActionThread t = new FileActionThread(new FileSplitterByPartNumber(f,c.getPartNumber()));
//					threads.put(c.getId(), t);
//					break;
//				}
//				}
//				
//			}
//			
//			threads.forEach((id,t) -> {
//				t.start();
//				view.setProcessStatus(id, ProcessStatus.Running);
//			});
//			
//			new ProgressThread(threads).start();
//			view.setEnabledStartButton(false);
		}
	}

//	class FileListSelectionListener implements ListSelectionListener {
//		@Override
//		public void valueChanged(ListSelectionEvent e) {
//			AppView view = getView();
//			File current = view.getSelectedFile();
//			if (current != null) {
//				view.showStatus("Current File: " + current.getPath());
//				view.loadConfig();
//			} else {
//				view.showStatus("");
//			}
//		}
//	}
//	
//	
//	class SplitOptionItemListener implements ItemListener{
//	    @Override
//	    public void itemStateChanged(ItemEvent event) {
//	       if (event.getStateChange() == ItemEvent.SELECTED) {
//	          SplitOption item = (SplitOption) event.getItem();
//	          System.out.println(item);
//	       }
//	    }
//	}
//
//	class DetailsPanelFocusListener implements FocusListener {
//
//		@Override
//		public void focusGained(FocusEvent e) {
//
//		}
//
//		@Override
//		public void focusLost(FocusEvent e) {
//			view.saveConfig();
//		}
//
//	}
//
//	class ProgressThread extends Thread {
//		HashMap<UUID,FileActionThread> threads;
//		
//		public ProgressThread(HashMap<UUID,FileActionThread> threads) {
//			this.threads = threads;
//		}
//		
//		@Override
//		public void run() {
//			boolean someoneAlive = true;
//			while (someoneAlive) {
//				someoneAlive = false;
//				for(Map.Entry<UUID, FileActionThread> entry : threads.entrySet()) {
//				    FileActionThread t = entry.getValue();
//				    UUID id = entry.getKey();
//				    if (t.isAlive()) {
//				    	someoneAlive = true;
//				    	view.setPercentage(id,t.getPercentage());
//				    }
//				    else {
//				    	view.setProcessStatus(id, ProcessStatus.Completed);
//				    }
//				}
//			}
//			view.setEnabledStartButton(true);
//		}
//	}
}
