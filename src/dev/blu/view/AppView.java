package dev.blu.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.swing.text.NumberFormatter;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.UUID;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.JLabel;

import dev.blu.model.GUI.enums.ActionType;
import dev.blu.model.core.SplitConfiguration;
import dev.blu.model.enums.ByteUnit;
import net.miginfocom.swing.MigLayout;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.JTable;

public class AppView extends JFrame {

	private JPanel contentPane;
	private JPanel detailsPanel;
	private JComboBox<ActionType> actionTypes;
	private JFormattedTextField txtSize;
	private JComboBox<ByteUnit> unitSelector;
	private JFormattedTextField txtParts;
	private JPasswordField passwordField;
	private JTextField txtOutputDir;
	private JFileChooser fileChooser;
	private JButton dirButton;
	private JButton addButton;
	private JButton removeButton;
	private JButton startButton;
//	private FileTableModel ftm;
	private JLabel lblStatus;
	private JTable fileList;
	private HashMap<UUID, SplitConfiguration> configs;
	private JButton stopButton;

	public AppView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignOnBaseline(true);
		panel.add(panel_1, BorderLayout.SOUTH);

		addButton = new JButton("Add");
		panel_1.add(addButton);
		addButton.setHorizontalAlignment(SwingConstants.LEFT);

		removeButton = new JButton("Remove");
		panel_1.add(removeButton);

		startButton = new JButton("Start");
		panel_1.add(startButton);
		
		stopButton = new JButton("Stop");
		panel_1.add(stopButton);
		stopButton.setEnabled(false);

		detailsPanel = new JPanel();
		panel.add(detailsPanel, BorderLayout.NORTH);
		detailsPanel.setLayout(new MigLayout("", "[240px,grow]", "[][24px][][24px][]"));

		actionTypes = new JComboBox<ActionType>();
		detailsPanel.add(actionTypes, "flowx,cell 0 0,alignx left");

		JLabel lblPartSize = new JLabel("Part Size:");
		detailsPanel.add(lblPartSize, "flowx,cell 0 1,alignx left,aligny center");

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		detailsPanel.add(horizontalGlue_1, "cell 0 1");


		NumberFormatter intFormatter = new NumberFormatter(NumberFormat.getInstance());
		intFormatter.setValueClass(Integer.class);
		intFormatter.setMinimum(0);
		intFormatter.setMaximum(Integer.MAX_VALUE);
		intFormatter.setAllowsInvalid(true);
		
		txtSize = new JFormattedTextField(intFormatter);
		detailsPanel.add(txtSize, "cell 0 1,growx,aligny center");
		txtSize.setColumns(10);

		unitSelector = new JComboBox<ByteUnit>();
		unitSelector.addItem(ByteUnit.B);
		unitSelector.addItem(ByteUnit.KiB);
		unitSelector.addItem(ByteUnit.MiB);
		unitSelector.addItem(ByteUnit.GiB);
		detailsPanel.add(unitSelector, "cell 0 1,growx");

		JLabel lblPartNumber = new JLabel("# of parts:");
		detailsPanel.add(lblPartNumber, "flowx,cell 0 2,alignx left");

		txtParts = new JFormattedTextField(intFormatter);
		detailsPanel.add(txtParts, "cell 0 2,growx,aligny center");
		txtParts.setColumns(10);

		Component horizontalGlue = Box.createHorizontalGlue();
		detailsPanel.add(horizontalGlue, "cell 0 0,growx");

		JLabel lblPassword = new JLabel("Password:");
		detailsPanel.add(lblPassword, "flowx,cell 0 3,alignx left");

		passwordField = new JPasswordField();
		detailsPanel.add(passwordField, "cell 0 3,growx");

		JLabel lblOutputDir = new JLabel("Output dir:");
		detailsPanel.add(lblOutputDir, "flowx,cell 0 4,alignx left");

		txtOutputDir = new JTextField();
		detailsPanel.add(txtOutputDir, "cell 0 4,growx");
		txtOutputDir.setColumns(10);

		dirButton = new JButton("...");
		dirButton.setPreferredSize(new Dimension(40, 19));
		detailsPanel.add(dirButton, "cell 0 4,alignx right");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		fileList = new JTable();
		fileList.setRowSelectionAllowed(true);

		scrollPane.setViewportView(fileList);

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		contentPane.add(panel_2, BorderLayout.SOUTH);

		lblStatus = new JLabel("");
		lblStatus.setFont(new Font("Dialog", Font.PLAIN, 10));
		panel_2.add(lblStatus);

		Component verticalStrut = Box.createVerticalStrut(15);
		panel_2.add(verticalStrut);

		fileChooser = new JFileChooser();
		configs = new HashMap<UUID, SplitConfiguration>();
	}
	
	public JFileChooser getFileChooser() {
		return this.fileChooser;
	}


	public void addAddButtonActionListener(ActionListener buttonActionListener) {
		addButton.addActionListener(buttonActionListener);
	}

	public void addRemoveButtonActionListener(ActionListener buttonActionListener) {
		removeButton.addActionListener(buttonActionListener);
	}

	public void addStartButtonActionListener(ActionListener buttonActionListener) {
		startButton.addActionListener(buttonActionListener);
	}
	
	public void addStopButtonActionListener(ActionListener buttonActionListener) {
		stopButton.addActionListener(buttonActionListener);
	}
	
	public void addDirButtonActionListener(ActionListener buttonActionListener) {
		dirButton.addActionListener(buttonActionListener);
	}

	public void addFileListSelectionListener(ListSelectionListener listSelectionListener) {
		fileList.getSelectionModel().addListSelectionListener(listSelectionListener);
	}

	public void addDetailsPanelFocusListener(FocusListener fl) {
		for (Component c : detailsPanel.getComponents()) {
			c.addFocusListener(fl);
		}
	}
	
	public void addActionTypeItemListener(ItemListener itemListener) {
		actionTypes.addItemListener(itemListener);
	}
	
	public void setTableModel(TableModel m) {
		fileList.setModel(m);
	} //AppModel holds the table model
	
//	public void addFile(File file, boolean selectNewFile) {
//		System.out.println("added file " + file.getName());
//		if (file != null) {
//			ftm.addFile(file);
//			selectRows(ftm.getRowCount() - 1, ftm.getRowCount() - 1);
//		}
//	}
//	public void addFile(File file) {
//		addFile(file, true);
//	}
//
//	public void removeFile(int index) {
//		if (ftm.removeFileAt(index) != 0)
//			return;
//		if (ftm.getRowCount() != 0) {
//			if (rowExists(index)) // user deleted a row in the middle
//				selectRows(index, index);
//			else // user deleted the last row
//				selectRows(index - 1, index - 1);
//		}
//	}

	public int selectRows(int row0, int row1) {
		fileList.clearSelection();
		try {
			fileList.addRowSelectionInterval(row0, row1);
		} catch (IllegalArgumentException e) {
			return 1;
		}
		return 0;
	}


	public boolean rowExists(int row) {
		return row < fileList.getRowCount();
	}

	public int getSelectedIndex() {
		return fileList.getSelectedRow();
	}

	public void showStatus(String status) {
		lblStatus.setText(status);
	}
	
	private SplitConfiguration getConfigById(UUID id) {
		if (id == null)
			return null;
		SplitConfiguration config = configs.get(id);
		if (config == null) {
			config = new SplitConfiguration( id );
			configs.put(id, config);
		}
		return config;
	}

//	public void loadConfig() {
//		SplitConfiguration config = getCurrentConfig();
//		if (config == null) // no file selected
//			return;
//
//		actionTypes.setSelectedItem(config.getSplitOption());
//		setValueByNumber(config.getPartNumber(), txtParts);
//		setValueByNumber(config.getPartSize(), txtSize);
//		setPassword(config.getPw());
//		unitSelector.setSelectedItem(config.getUnit());
//
//		// TODO implement output dir load
//	}

//	private void setPassword(char[] pw) {
//		String p = "";
//		if (pw != null) {
//			for (char c : pw) {
//				p = p + c;
//			} 			
//		}
//		passwordField.setText(p);
//	}

//	public void saveConfig() {
//		SplitConfiguration config = getCurrentConfig();
//		if (config == null) // no file selected
//			return;
//
//		config.setSplitOption((SplitOption) actionTypes.getSelectedItem());
//		if (txtParts.getValue() instanceof Long)
//			config.setPartNumber((long) txtParts.getValue());
//		if (txtSize.getValue() instanceof Long)
//			config.setPartSize((long) txtSize.getValue());
//		config.setPw(passwordField.getPassword());
//		config.setUnit((ByteUnit) unitSelector.getSelectedItem());
//
//		// TODO implement output dir save
//	}


	private void setValueByNumber(long i, JFormattedTextField txt) {
		if (i == -1) {
			txt.setValue(null);
		} else
			txt.setValue(i);
	}

//	public Vector<SplitConfiguration> getQueue() {
//		Vector<SplitConfiguration> queue = new Vector<SplitConfiguration>(); 
//		
//		for (int i = 0; i<ftm.getRowCount(); i++) {
//			SplitConfiguration config = getConfig(i);
//			queue.add(config);
//		}
//		
//		return queue;
//	}
//
//	public void setProcessStatus(UUID id, ProcessStatus ps) {
//		ftm.setProcessStatus(id, ps);
//	}
//
//	public File getFile(UUID id) {
//		return ftm.getFile(id);
//	}
//
//	public void setPercentage(UUID id, double percentage) {
//		ftm.setPercentage(id, percentage);
//	}
//
//	public void setEnabledStartButton(boolean b) {
//		startButton.setEnabled(b);
//	}
//	
//	public void addSplitOptionsItemListener(ItemListener itemListener) {
//		actionTypes.addItemListener(itemListener);
//	}
	
	
	
	/*
	 * public ActionType getActionType() { return
	 * actionTypes.getItemAt(actionTypes.getSelectedIndex()); }
	 * 
	 * public long getPartSize() { return Long.parseLong(txtSize.getText()); }
	 * 
	 * public int getPartsNumber() { return Integer.parseInt(txtParts.getText()); }
	 * 
	 * public char[] getPassword() { return passwordField.getPassword(); }
	 * 
	 * public String getOutputDir() { return txtOutputDir.getText(); }
	 */

	public JPanel getDetailsPanel() {
		return detailsPanel;
	}
	public JComboBox<ActionType> getActionTypes() {
		return actionTypes;
	}

	public JFormattedTextField getTxtSize() {
		return txtSize;
	}

	public JFormattedTextField getTxtParts() {
		return txtParts;
	}

	public JPasswordField getPasswordField() {
		return passwordField;
	}

	public JTextField getTxtOutputDir() {
		return txtOutputDir;
	}

	public JComboBox<ByteUnit> getUnitSelector() {
		return unitSelector;
	}

	public JButton getAddButton() {
		return addButton;
	}
	
	public JButton getRemoveButton() {
		return removeButton;
	}
	
	public JButton getStartButton() {
		return startButton;
	}

	public JButton getStopButton() {
		return stopButton;
	}

}
