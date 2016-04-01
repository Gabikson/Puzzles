import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class Puzzle extends JPanel implements MouseListener,
		MouseMotionListener {

	private static final long serialVersionUID = 7863830151322062680L;

	private boolean isSelected;

	private int x1;
	private int y1;

	private PuzzleService puzzleService;

	private Dimension dimension;

	private int index;

	private BufferedImage image;

	private boolean isBad;

	private int angle;

	public Puzzle() {
	}

	public Puzzle(BufferedImage img, int index, PuzzleService puzzleService) {
		this.image = img;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setDimension(new Dimension(img.getWidth(), img.getHeight()));
		setBounds(0, 0, img.getWidth(), img.getHeight());
		setPreferredSize(this.dimension);
		this.index = index;
		this.puzzleService = puzzleService;
	}

	public boolean isBad() {
		return isBad;
	}

	public void setBad(boolean isBad) {
		this.isBad = isBad;
		this.repaint();
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		this.repaint();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
		setPreferredSize(this.dimension);
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
		if ((angle == 90 || angle == 270))
			this.setSize(this.image.getHeight(), this.image.getWidth());
		else
			this.setSize(this.image.getWidth(), this.image.getHeight());
		repaint();
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
		((JLayeredPane) this.getParent()).moveToFront(this);
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			isSelected = true;
			x1 = arg0.getX();
			y1 = arg0.getY();
		} else {
			if (arg0.getButton() == MouseEvent.BUTTON3) {
				rotate();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		isSelected = false;
		int d = 10;
		int x = this.getX();
		int y = this.getY();
		Component component;
		// left
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			component = this.getParent().getComponentAt(this.getX() - d,
					this.getY());
			if (component != null && component instanceof Puzzle) {
				x = component.getX() + component.getWidth();
				y = component.getY();
			}
			// top
			component = this.getParent().getComponentAt(this.getX(),
					this.getY() - d);
			if (component != null && component instanceof Puzzle) {
				x = component.getX();
				y = component.getY() + component.getHeight();
			}
			// right
			component = this.getParent().getComponentAt(
					this.getX() + this.getWidth() + d, this.getY());
			if (component != null && component instanceof Puzzle) {
				x = component.getX() - this.getWidth();
				y = component.getY();
			}
			// bottom
			component = this.getParent().getComponentAt(this.getX(),
					this.getY() + this.getHeight() + d);
			if (component != null && component instanceof Puzzle) {
				x = component.getX();
				y = component.getY() - this.getHeight();
			}
			this.setLocation(x, y);
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (isSelected) {
			int x = arg0.getX() + this.getX() - x1;
			int y = arg0.getY() + this.getY() - y1;
			if ((x + this.getWidth() < this.getParent().getWidth() && x > 0)) {
				this.setLocation(arg0.getX() + this.getX() - x1, this.getY());
			}
			if (y + this.getHeight() < this.getParent().getHeight() && y > 0) {
				this.setLocation(this.getX(), arg0.getY() + this.getY() - y1);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int offset = (this.getWidth() - this.getHeight()) / 2;
		Graphics2D g2d = (Graphics2D) g;
		g2d.rotate(Math.toRadians(angle), this.getWidth() / 2,
				this.getHeight() / 2);
		if (angle == 90 || angle == 270) {
			g2d.drawImage(image, offset, -offset, null);
			g2d.drawRect(offset, -offset, this.getHeight() - 1,
					this.getWidth() - 1);
		} else {
			g2d.drawImage(image, 0, 0, null);
			g2d.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
		}

		if (isBad) {
			g2d.setColor(Color.red);
			g2d.drawRect(offset, -offset, this.getWidth() - 2,
					this.getHeight() - 2);
		}
//		if (isSelected) {
//			 g2d.setColor(Color.green);
//			 g2d.drawRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
//		}
		// g2d.drawString(String.valueOf(this.index), 5, 15);
		g2d.dispose();
	}

	/* check if puzzel is a neighbor for current at the side */
	public int checkForNeighbor(Puzzle puzzle, Side side) {
		int start_x = 1;
		int start_y = 1;
		int nStart_x = 1;
		int nStart_y = 1;

		Direction direction = null;

		BufferedImage nImage = puzzle.getImage();

		switch (side) {
		case TOP: {
			nStart_y = nImage.getHeight();
			direction = Direction.RightToLeft;
			break;
		}
		case RIGHT: {
			start_x = image.getWidth();
			direction = Direction.TopToBottom;
			break;
		}
		case BOTTOM: {
			start_y = image.getHeight();
			direction = Direction.RightToLeft;
			break;
		}
		case LEFT: {
			nStart_x = nImage.getWidth();
			direction = Direction.TopToBottom;
			break;
		}
		}

		int ColorArray[] = null;
		int nColorArray[] = null;

		if (direction == Direction.RightToLeft) {
			ColorArray = new int[image.getWidth()];
			nColorArray = new int[nImage.getWidth()];

			for (int x = 0; x < ColorArray.length; x++) {
				ColorArray[x] = image.getRGB(x, start_y - 1);
			}

			for (int x = 0; x < nColorArray.length; x++) {
				nColorArray[x] = nImage.getRGB(x, nStart_y - 1);
			}

		} else if (direction == Direction.TopToBottom) {
			ColorArray = new int[image.getHeight()];
			nColorArray = new int[nImage.getHeight()];

			for (int y = 0; y < ColorArray.length; y++) {
				ColorArray[y] = image.getRGB(start_x - 1, y);
			}

			for (int y = 0; y < nColorArray.length; y++) {
				nColorArray[y] = nImage.getRGB(nStart_x - 1, y);
			}

		}

		int trueCount = 0;
		int falseCount = 0;

		for (int i = 0; i < ColorArray.length; i++) {
			if (colorCompare(ColorArray[i], nColorArray[i],
					puzzleService.getColorDistance())) {
				trueCount++;
			} else {
				falseCount++;
			}
		}
		return (trueCount > falseCount)?trueCount:0;
	}

	private boolean colorCompare(int color1, int color2, int colorDistance) {
		MostColor mostColor1 = null;
		MostColor mostColor2 = null;
		int r1 = (color1 & 0x00ff0000) >> 16;
		int g1 = (color1 & 0x0000ff00) >> 8;
		int b1 = color1 & 0x000000ff;

		int r2 = (color2 & 0x00ff0000) >> 16;
		int g2 = (color2 & 0x0000ff00) >> 8;
		int b2 = color2 & 0x000000ff;

		if (r1 >= g1) {
			if (r1 >= b1) {
				mostColor1 = MostColor.isRed;
			} else {
				mostColor1 = MostColor.isBlue;
			}
		} else {
			if (g1 >= b1) {
				mostColor1 = MostColor.isGreen;
			} else {
				mostColor1 = MostColor.isBlue;
			}
		}

		if (r2 >= g2) {
			if (r2 >= b2) {
				mostColor2 = MostColor.isRed;
			} else {
				mostColor2 = MostColor.isBlue;
			}
		} else {
			if (g2 >= b2) {
				mostColor2 = MostColor.isGreen;
			} else {
				mostColor2 = MostColor.isBlue;
			}
		}

		return (Math.abs(r1 - r2) <= colorDistance)
				&& (Math.abs(g1 - g2) <= colorDistance)
				&& (Math.abs(b1 - b2) <= colorDistance);
		// && mostColor1 == mostColor2;
		// return mostColor1 == mostColor2;
	}

	private void rotate() {
		if (this.angle >= 270) {
			this.angle = 0;
		} else {
			this.angle += 90;
		}
		this.setSize(this.getHeight(), this.getWidth());
		// this.repaint();
	}

	public PuzzleService getPuzzleService() {
		return puzzleService;
	}

	public void setPuzzleService(PuzzleService puzzleService) {
		this.puzzleService = puzzleService;
	}

}
