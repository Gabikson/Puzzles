import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;

public class Desk extends JLayeredPane implements MouseMotionListener,
		MouseListener {

	private int x, y, x1, y1, w, h, w1, h1;

	private boolean mousepressed;

	private BufferedImage bg;

	public Desk(LayoutManager lm) {
		// super(lm);
		try {
			bg = ImageIO.read(Image.class.getResourceAsStream("/bg.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

		w1 = arg0.getX();
		h1 = arg0.getY();

		if (w1 >= x) {
			w = w1 - x;
		} else {
			x1 = w1;
			w = x - w1;
		}

		if (h1 >= y) {
			h = h1 - y;
		} else {
			y1 = h1;
			h = y - y1;
		}

		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		this.mousepressed = true;
		x = arg0.getX();
		y = arg0.getY();

		x1 = arg0.getX();
		y1 = arg0.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		this.mousepressed = false;
		x1 = arg0.getX();
		y1 = arg0.getY();
		this.normalize();
		this.repaint();
		selectElement();
	}

	private void selectElement() {
		for (Component c : getComponents()) {
			Puzzle puzzle = (Puzzle) c;
			if ((puzzle.getX() >= x & puzzle.getX() <= x1 & puzzle.getY() >= y & puzzle
					.getY() <= y1)
					& (puzzle.getX() + puzzle.getWidth() >= x & puzzle.getX()
							+ puzzle.getWidth() <= x1))

			{
				puzzle.setSelected(true);
			} else {
				puzzle.setSelected(false);
			}

		}
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(bg,0,0,getWidth(), getHeight(),null);
		super.paint(g);
		if (mousepressed) {
			 g2d.setXORMode(Color.BLACK);
			 g2d.setColor(Color.YELLOW);
			 g2d.fillRect(x1, y1, w, h);
			 g2d.setColor(Color.GREEN);
			 g2d.drawRect(x1, y1, w, h);
		} else {
			// g2d.drawString(x+","+y, 5, 15);
			// g2d.drawString(String.valueOf(w)+","+String.valueOf(h), w, h);
//			g2d.setColor(Color.red);
//			g2d.drawRect(x, y, x1 - x, y1 - y);

			g2d.dispose();
		}

	}

	private void normalize() {
		int tmp = 0;
		if (x > x1) {
			tmp = x;
			x = x1;
			x1 = tmp;
		}
		if (y > y1) {
			tmp = y;
			y = y1;
			y1 = tmp;
		}
	}

}
