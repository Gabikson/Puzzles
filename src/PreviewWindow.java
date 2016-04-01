import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class PreviewWindow extends JFrame {

	private static final long serialVersionUID = 1522221628473074173L;

	private BufferedImage image;

	public PreviewWindow(String title) {
		this.setTitle(title);
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		this.setSize(this.image.getWidth(),this.image.getHeight());
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) this.rootPane.getGraphics();
		g2d.drawImage(image, 0, 0,this.getWidth(), this.getHeight(), null);
	}

}
