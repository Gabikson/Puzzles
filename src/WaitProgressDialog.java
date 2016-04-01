import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

public class WaitProgressDialog extends JFrame implements Progressable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar = new JProgressBar();
	private Border border = BorderFactory.createTitledBorder("Please wait...");

	public WaitProgressDialog() {
		this(100);
	}

	public WaitProgressDialog(int MaxProgress) {
		setPreferredSize(new Dimension(500, 50));
		setUndecorated(true);
		setAlwaysOnTop(true);
		progressBar.setMaximum(MaxProgress);
		progressBar.setPreferredSize(new Dimension(500, 30));
		progressBar.setStringPainted(true);
		progressBar.setForeground(Color.orange);  
	    progressBar.setBorder(border);
		add(progressBar);
		setVisible(true);
		pack();
		setLocationRelativeTo(null);
	}

	public void setProgress(int i) {
		this.progressBar.setValue(i);
		this.repaint();
		this.invalidate();
	}

	public void closeWindow() {
		this.dispose();
	}

	@Override
	public void setMaxprogress(int maxprogress) {
		this.progressBar.setMaximum(maxprogress);
	}
}
