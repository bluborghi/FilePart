package dev.blu.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JLayeredPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JLabel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import dev.blu.model.FileTableModel;

import com.jgoodies.forms.layout.FormSpecs;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import net.miginfocom.swing.MigLayout;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.JTable;

public class AppView extends JFrame {

	private JPanel contentPane;
	private JTextField txtSize;
	private JTextField txtParts;
	private JPasswordField passwordField;
	private JTextField txtOutputDir;
	private JFileChooser fileChooser;
	private JButton addButton;
	private JButton removeButton;
	private JButton startButton;
	private DefaultListModel<File> dlm;
	private FileTableModel ftm;
	private JLabel lblStatus;
	private JTable fileList;
	
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
		
		JLayeredPane layeredPane = new JLayeredPane();
		panel.add(layeredPane, BorderLayout.NORTH);
		layeredPane.setLayout(new MigLayout("", "[240px,grow]", "[][24px][][24px][]"));
		
		JComboBox splitOptions = new JComboBox();
		layeredPane.add(splitOptions, "flowx,cell 0 0,alignx left");
		
		JLabel lblPartSize = new JLabel("Part Size:");
		layeredPane.add(lblPartSize, "flowx,cell 0 1,alignx left,aligny center");
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		layeredPane.add(horizontalGlue_1, "cell 0 1");
		
		txtSize = new JTextField();
		layeredPane.add(txtSize, "cell 0 1,growx,aligny center");
		txtSize.setColumns(10);
		
		JComboBox unitSelector = new JComboBox();
		layeredPane.add(unitSelector, "cell 0 1,growx");
		
		JLabel lblPartNumber = new JLabel("# of parts:");
		layeredPane.add(lblPartNumber, "flowx,cell 0 2,alignx left");
		
		txtParts = new JTextField();
		layeredPane.add(txtParts, "cell 0 2,growx,aligny center");
		txtParts.setColumns(10);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		layeredPane.add(horizontalGlue, "cell 0 0,growx");
		
		JLabel lblPassword = new JLabel("Password:");
		layeredPane.add(lblPassword, "flowx,cell 0 3,alignx left");
		
		passwordField = new JPasswordField();
		layeredPane.add(passwordField, "cell 0 3,growx");
		
		JLabel lblOutputDir = new JLabel("Output dir:");
		layeredPane.add(lblOutputDir, "flowx,cell 0 4,alignx left");
		
		txtOutputDir = new JTextField();
		layeredPane.add(txtOutputDir, "cell 0 4,growx");
		txtOutputDir.setColumns(10);
		
		JButton dirButton = new JButton("...");
		dirButton.setPreferredSize(new Dimension(40, 19));
		layeredPane.add(dirButton, "cell 0 4,alignx right");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		 
		fileList = new JTable(ftm);
		fileList.setRowSelectionAllowed(true);

		scrollPane.setViewportView(fileList);
		
		dlm = new DefaultListModel<File>();
		
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
	}
	
	public JFileChooser getFileChooser() {
		return this.fileChooser;
	}

	public void setAddButtonActionListener(ActionListener buttonActionListener) {
		addButton.addActionListener(buttonActionListener);
	}

	public void setRemoveButtonActionListener(ActionListener buttonActionListener) {
		removeButton.addActionListener(buttonActionListener);
	}

	public void setStartButtonActionListener(ActionListener buttonActionListener) {
		startButton.addActionListener(buttonActionListener);
	}

	public void addFile(File file, boolean selectNewFile) {
		if (file!= null) 
			ftm.addFile(file);
		//TODO selectNewFile logic!!!
		//fileList.changeSelection(rowIndex, columnIndex, toggle, extend);
	}

	public void addFile(File file) {
		//dlm.addElement(file);
		ftm.addFile(file);
	}
	
	public void removeFile(int index) {
		//System.out.println(dlm);
		//dlm.removeElement((Object) file);
		//System.out.println(dlm);
		ftm.removeFileAt(index);
	}

	public void setFileListSelectionListener(ListSelectionListener listSelectionListener) {
		fileList.getSelectionModel().addListSelectionListener(listSelectionListener);
	}

	public File getSelectedFile() {
		int row = fileList.getSelectedRow();
		if (row == -1) 
			return null;
		else
			return (File) fileList.getValueAt(row, 0);
	}
	
	public void showStatus(String status) {
		lblStatus.setText(status);
	}

	

	
}
