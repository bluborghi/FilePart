package dev.blu.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.ParseException;
import java.util.Collections;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dev.blu.model.AppModel;
import dev.blu.model.GUI.DetailsPanelOptions;
import dev.blu.model.GUI.GUIModel;
import dev.blu.model.GUI.enums.ActionType;
import dev.blu.model.core.FileActionThread;
import dev.blu.model.core.FileConfiguration;
import dev.blu.model.core.SplitConfiguration;
import dev.blu.model.enums.ByteUnit;
import dev.blu.model.helpers.FileHelper;
import dev.blu.view.AppView;

public class AppController {
	private AppView view;
	private GUIModel model;
	private int lastIndex; 
	
	public AppController(GUIModel m, AppView v) {
		setView(v);
		setModel(m);
		setLastIndex(-1);
		
		view.setTableModel(model.getTableModel());
		
		view.addDirButtonActionListener(new DirButtonActionListener());
		view.addAddButtonActionListener(new AddButtonActionListener());
		view.addRemoveButtonActionListener(new RemoveButtonActionListener());
		view.addStartButtonActionListener(new StartButtonActionListener());;
		view.addStopButtonActionListener(new StopButtonActionListener());
		view.addFileListSelectionListener(new FileListSelectionListener());
		view.addDetailsPanelFocusListener(new DetailsPanelFocusListener());
		view.addActionTypeItemListener(new ActionTypeItemListener());
//		view.addDetailsPanelPropertyChangeListener(new DetailsPanelPropertyChangeListener());
//		view.addSplitOptionsItemListener(new SplitOptionItemListener());
		
		initActionTypes(view.getActionTypes());

		view.setVisible(true);
	}

	private void setView(AppView view) {
		this.view = view;
	}	

	private void setModel(GUIModel model) {
		this.model = model;
	}
	
	private int getLastIndex() {
		return lastIndex;
	}

	private void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}

	private void saveDetailsPanel(int index) {
		if (index == -1) {
			index = getLastIndex();
			if (index == -1)
				return;
		}

		FileConfiguration fc = model.getFileConfigAt(index);
		if (fc == null) // no file selected
			return;

		SplitConfiguration current = fc.getSplitConfig();
		
		JComboBox<ActionType> actionTypes = view.getActionTypes();
		model.getSidePanelAt(index).setActionType((ActionType) actionTypes.getSelectedItem());
		
		
		JComboBox<ByteUnit> unitSelector = view.getUnitSelector();
		
		long partSize = current != null ? current.getPartSize() : 0;
		JFormattedTextField txtSize = view.getTxtSize();
		try {
			txtSize.commitEdit();
			if (txtSize.getValue() != null) { //bug workaround, getValue() changes its return type randomly
				Class c = txtSize.getValue().getClass();
//				System.out.println(c);
				if (c.equals(Long.class))
					partSize = (long) txtSize.getValue();
				else if (c.equals(Integer.class))
					partSize = ((int) txtSize.getValue());
			}
		} catch (ParseException e) {
			partSize = 0;
		}
		
		int partNumber = current != null ? current.getPartNumber() : 0;
		JFormattedTextField txtParts = view.getTxtParts();
		try {
			txtParts.commitEdit();
			if (txtParts.getValue() != null) {
				partNumber = (int) txtParts.getValue();
			}
		} catch (ParseException e) {
			partNumber = 0;
		}
		
		JTextField txtOutputDir = view.getTxtOutputDir();
		JPasswordField passwordField = view.getPasswordField();		
		
		ByteUnit byteUnit = (ByteUnit) unitSelector.getSelectedItem();
		char[] password = passwordField.getPassword();
		String outputDir = txtOutputDir.getText();
		
		SplitConfiguration sc = new SplitConfiguration(fc.getId(), partNumber, partSize, byteUnit, password, outputDir);
		model.updateConfig(fc.getId(), sc);
	}
	
	private void loadDetailsPanel(int index) {
		DetailsPanelOptions dpc = model.getSidePanelAt(index);
		
		FileConfiguration fc = model.getFileConfigAt(index);
		SplitConfiguration sc = fc.getSplitConfig();
		if (sc == null) {
			sc = new SplitConfiguration( fc.getId() );
			model.updateConfig(fc.getId(), sc);
		}
		
		JComboBox<ActionType> actionTypes = view.getActionTypes();
		JFormattedTextField txtSize = view.getTxtSize();
		JComboBox<ByteUnit> unitSel = view.getUnitSelector();
		JFormattedTextField txtParts = view.getTxtParts();
		JTextField txtOutputDir = view.getTxtOutputDir();
		JPasswordField passwordField = view.getPasswordField();
		
		actionTypes.setSelectedItem(dpc.getActionType());
		txtSize.setValue(sc.getPartSize());
		unitSel.setSelectedItem(sc.getUnit());
		txtParts.setValue(sc.getPartNumber());
		txtOutputDir.setText(sc.getOutputDir());
		passwordField.setText(new String(sc.getPw()));			
	}

	private void initActionTypes(JComboBox<ActionType> actionTypes) {
		if (actionTypes.getItemCount() == 0) {
			actionTypes.addItem(ActionType.SplitByMaxSize);
			actionTypes.addItem(ActionType.SplitByNumberOfParts);
			actionTypes.addItem(ActionType.Merge);
		}
	}

	class DirButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("add action performed");
			JFileChooser fc = view.getFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setMultiSelectionEnabled(false);
			
			int returnVal = fc.showOpenDialog(view);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				//System.out.println("Opening: " + file.getAbsolutePath());
				view.getTxtOutputDir().setText(file.getPath());
			} else {
				//System.out.println("Open command cancelled by user.");
			}
		}
	}

	class AddButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("add action performed");
			JFileChooser fc = view.getFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(true);
			
			int returnVal = fc.showOpenDialog(view);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File[] files = fc.getSelectedFiles();
				//System.out.println("Opening: " + file.getAbsolutePath());
				for (File file : files) {
					model.addFile(file);
				}
				int index = model.getConfigsCount() -1;
				view.selectRows(index, index);					
				//model.updateConfig(id, new SplitConfiguration(id, 3, 0, ByteUnit.B, "password".toCharArray(), "/run/media/blubo/Volume/FilePart/myFolder/mySecondOtherFolder"));
			} else {
				//System.out.println("Open command cancelled by user.");
			}
		}
	}

	class RemoveButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// System.out.println("remove action performed");
			int[] reverseIndexArray = reverse(view.getSelectedRows());
			
			for (int file_index : reverseIndexArray)
			if (file_index != -1) {
//				System.out.println(model.getTableModel().getConfig(file_index).getSplitConfig().getPartSize());
				model.removeFileAt(file_index);
//				System.out.println(model.getTableModel().getConfig(file_index).getSplitConfig().getPartSize());
				int count = model.getConfigsCount();
				if(count>0) {//if there are elements left
					if (file_index < count) {
						view.selectRows(file_index, file_index);
					}
					else {
						view.selectRows(count -1, count -1);	
					}
				}
				else {
					setLastIndex(-1);
				}
			}
		}

		private int[] reverse(int a[]) 
		{ 
			int n = a.length;
			int[] b = new int[n]; 
			int j = n; 
			for (int i = 0; i < n; i++) { 
				b[j - 1] = a[i]; 
				j = j - 1; 
			} 
			return b;
		} 
	}


	class FileListSelectionListener implements ListSelectionListener {
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			int lastIndex = getLastIndex();
			int index = view.getSelectedIndex();
			if (index != -1) { //if this selection is valid, save the previous selection and load new data
				if (lastIndex != -1) { //if last selection was null, don't save its data
					saveDetailsPanel(lastIndex); 
				}
				setLastIndex(index);
				loadDetailsPanel(index);				
			}
			else { //if this selection is null, don't save data next time
				setLastIndex(index);
			}	
		}
		
	}
	
	class DetailsPanelFocusListener implements FocusListener {
		
		@Override
		public void focusGained(FocusEvent e) {
			
		}

		@Override
		public void focusLost(FocusEvent e) {
			saveDetailsPanel(view.getSelectedIndex());
		}

	
	}
	
	class ActionTypeItemListener implements ItemListener{
	    @Override
	    public void itemStateChanged(ItemEvent event) {
	       if (event.getStateChange() == ItemEvent.SELECTED) {
	          ActionType action = (ActionType) event.getItem();
//	          System.out.println("Action type changed, current: "+action);
	          disableUnnecessaryFields(action);
	       }
	    }

	}

	private void disableUnnecessaryFields(ActionType action) {
		switch (action) {
			case SplitByMaxSize:{
				view.getTxtSize().setEnabled(true);
				view.getUnitSelector().setEnabled(true);
				view.getTxtParts().setEnabled(false);
				break;
			}
			case SplitByNumberOfParts:{
				view.getTxtSize().setEnabled(false);
				view.getUnitSelector().setEnabled(false);
				view.getTxtParts().setEnabled(true);
				break;
			}
			case Merge:{
				view.getTxtSize().setEnabled(false);
				view.getUnitSelector().setEnabled(false);
				view.getTxtParts().setEnabled(false);
				break;
			}
		}
	}      
	
	private void setExecutionInterfaceEnabled(boolean enabled) {
		for (Component c : view.getDetailsPanel().getComponents()) {
			c.setEnabled(!enabled);
		}
		view.getAddButton().setEnabled(!enabled);
		view.getRemoveButton().setEnabled(!enabled);
		view.getStartButton().setEnabled(!enabled);
		view.getStopButton().setEnabled(enabled);
		
		if (!enabled) {
			 ActionType action = (ActionType) view.getActionTypes().getSelectedItem();
	         disableUnnecessaryFields(action);
		}
	}
	
	class StopButtonActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.stopThreads();
			new SetExecutionInterfaceEnabledThread(false).start();
		}
		
	}
	
	class SetExecutionInterfaceEnabledThread extends Thread {
		boolean value;
		
		public SetExecutionInterfaceEnabledThread(boolean value) {
			this.value = value;
		}
		
		@Override
		public void run() {
			while(model.isRunning()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setExecutionInterfaceEnabled(value);
		}
	}

	class StartButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {    	
	     	setExecutionInterfaceEnabled(true);
			
			
	    	Vector<FileActionThread> threads = model.prepareThreads();
	    	
	    	for (FileActionThread t : threads) {
	    		if (t.hasErrors())
	    			System.err.print(t.getErrorMessage());
	    	}
	    	
	    	model.startThreads();

			new SetExecutionInterfaceEnabledThread(false).start();
	    	
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
