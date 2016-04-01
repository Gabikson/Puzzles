import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class OpenImageDialog extends JDialog implements ActionListener {

	public final static int DIALOG_OK = 1;
	public final static int DIALOG_CANCEL = 0;

	private int result;

	private static final long serialVersionUID = 1L;

	private JLabel fileLabel = new JLabel("File");
	private JTextField pathEdit = new JTextField();
	private JButton openFileBtn = new JButton("...");
	private JLabel fileLabel1 = new JLabel("Folder");
	private JTextField pathEditSave = new JTextField();
	private JButton saveFileBtn = new JButton("...");
	private JLabel countLabel = new JLabel("Chunks");
	private JSpinner chunksCount = new JSpinner(new SpinnerNumberModel(3, 0,
			100, 1));
	private JButton okbtn = new JButton("OK");
	private JButton cancelBtn = new JButton("Cancel");

	private JPanel filePanel = new JPanel();
	private JPanel filePanelSave = new JPanel();
	private JPanel chunkspanel = new JPanel();
	private JPanel btnPanel = new JPanel();

	public OpenImageDialog(JFrame owner) {
		super(owner);
		this.setLayout(new BorderLayout());

		filePanel.add(fileLabel);
		pathEdit.setPreferredSize(new Dimension(200, 20));
		filePanel.add(pathEdit);
		filePanel.add(openFileBtn);

		filePanelSave.add(fileLabel1);
		pathEditSave.setPreferredSize(new Dimension(200, 20));
		filePanelSave.add(pathEditSave);
		filePanelSave.add(saveFileBtn);

		filePanel.add(filePanelSave);
		filePanel.setPreferredSize(new Dimension(0, 80));

		chunkspanel.add(countLabel);
		chunksCount.setPreferredSize(new Dimension(50, 20));
		chunkspanel.add(chunksCount);

		btnPanel.add(okbtn);
		btnPanel.add(cancelBtn);

		openFileBtn.addActionListener(this);
		saveFileBtn.addActionListener(this);
		okbtn.addActionListener(this);
		cancelBtn.addActionListener(this);

		this.add(filePanel, BorderLayout.NORTH);
		this.add(chunkspanel, BorderLayout.CENTER);
		this.add(btnPanel, BorderLayout.SOUTH);
		setSize(300, 200);
		setResizable(false);
		setLocationRelativeTo(null);
		// setAlwaysOnTop(true);
		setModal(true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public int showOpenDialog() {
		setVisible(true);
		return result;
	}

	public File getFile() {
		return new File(this.pathEdit.getText());
	}
	
	public File getFolder() {
		return new File(this.pathEditSave.getText());
	}

	public int getChunksCount() {
		return (Integer) this.chunksCount.getValue();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(openFileBtn)) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Select image...");
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				pathEdit.setText(fc.getSelectedFile().getAbsolutePath());
			}
		} else if (e.getSource().equals(saveFileBtn)) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle("Select folder to save chunks...");
			if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
				pathEditSave.setText(fc.getSelectedFile().getPath());
			}
		} else if (e.getSource().equals(okbtn)) {
			File file;
			if (pathEdit.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "Please select a file!",
						"File not selected", JOptionPane.INFORMATION_MESSAGE);
			} else if (!((file = new File(pathEdit.getText())).exists() && file
					.isFile())) {
				JOptionPane.showMessageDialog(this, "Check correct file path!",
						"File not found", JOptionPane.ERROR_MESSAGE);
			} else {
				result = DIALOG_OK;
				this.dispose();
			}
		} else if (e.getSource().equals(cancelBtn)) {
			result = DIALOG_CANCEL;
			this.dispose();
		}

	}
}
