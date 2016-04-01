import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class PuzzleService {
	private final String chunkFileNamePrefix = "puzzleImg_";

	private int chunkWidth;
	private int chunkHeight;
	private int chunks;
	private String chunkImageFormat;
	private String chunksfolderPath;

	private int colorDistance;

	private Puzzle[] puzzles;

	public PuzzleService() {

	}

	public int getChunkWidth() {
		return chunkWidth;
	}

	public void setChunkWidth(int chunkWidth) {
		this.chunkWidth = chunkWidth;
	}

	public int getChunkHeight() {
		return chunkHeight;
	}

	public void setChunkHeight(int chunkHeight) {
		this.chunkHeight = chunkHeight;
	}

	public int getChunks() {
		return chunks;
	}

	public void setChunks(int chunks) {
		this.chunks = chunks;
	}

	public String getChunkImageFormat() {
		return chunkImageFormat;
	}

	public void setChunkImageFormat(String chunkImageFormat) {
		this.chunkImageFormat = chunkImageFormat;
	}

	public String getChunksFolderPath() {
		return chunksfolderPath;
	}

	public void setChunksFolderPath(String folderPath) {
		this.chunksfolderPath = folderPath;
	}

	public Puzzle[] getPuzzles() {
		return puzzles;
	}

	public void setPuzzles(Puzzle[] puzzles) {
		this.puzzles = puzzles;
	}

	public String getChunkFileNamePrefix() {
		return chunkFileNamePrefix;
	}

	/*
	 * Get puzzle position at [x,y]
	 * 
	 * return null if not found
	 */
	protected Puzzle getPuzzleAt(int x, int y) {
		for (Puzzle puzzle : puzzles) {
			if (puzzle.getX() == x && puzzle.getY() == y) {
				return puzzle;
			}
		}
		return null;
	}
	
	
	protected Side getRotatedSide(Side side, int angle) {
		Side s = side;
		if (angle >= 90 && angle<=270) {
			int divider = angle / 90;
			int order = side.ordinal();
			int a = divider + order;
			int index = (a >= Side.values().length) ? Math
					.abs(Side.values().length - a) : a;
			s = Side.values()[index];	
		}
		return s;
	}

	public boolean validation() {
		Puzzle p = null;
		int x = 0;
		int y = 0;

		boolean isNeighbor = false;
		for (int i = 0; i < puzzles.length; i++) {
			int okCount = 0;
			for (Side s : Side.values()) {
				switch (getRotatedSide(s, puzzles[i].getAngle())) {
				case TOP: {
					x = puzzles[i].getX();
					y = puzzles[i].getY() - puzzles[i].getHeight();
					break;
				}
				case RIGHT: {
					x = puzzles[i].getX() + puzzles[i].getWidth();
					y = puzzles[i].getY();
					break;
				}
				case BOTTOM: {
					x = puzzles[i].getX();
					y = puzzles[i].getY() + puzzles[i].getHeight();
					break;
				}
				case LEFT: {
					x = puzzles[i].getX() - puzzles[i].getWidth();
					y = puzzles[i].getY();
					break;
				}
				}
				p = getPuzzleAt(x, y);
				if (p != null) {
					isNeighbor = puzzles[i].checkForNeighbor(p, s) > 0;
					if (isNeighbor)
						okCount++;
				}
			}
//			 System.out.println("puzzle[" + i + "]=" + okCount);
			if (okCount == 0) {
				return false;
			}
		}

		return true;
	}
	
	public void build(final Progressable p) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int x = 0;
				int y = 0;
//				puzzles[0].setBad(true);
//				puzzles[0].setLocation(100, 100);
//				puzzles[0].setAngle(0);
				p.setMaxprogress(getChunks());
				for (int i = 0; i < puzzles.length - 1; i++) {
					p.setProgress(i);
					for (Side s : Side.values()) {
						int maxelement = i+1;
						int maxvalue = 0;
						for (int j = i + 1; j < puzzles.length; j++) {
							puzzles[j].setAngle(puzzles[0].getAngle());
							 int rate = puzzles[i].checkForNeighbor(puzzles[j], s);
							 if(rate > maxvalue){
								 maxvalue = rate;
								 maxelement = j;
							 }
							if (rate > 0) {
								switch (getRotatedSide(s, puzzles[i].getAngle())) {
								case TOP: {
									x = puzzles[i].getX();
									y = puzzles[i].getY()
											- puzzles[i].getHeight();
									break;
								}
								case RIGHT: {
									x = puzzles[i].getX()
											+ puzzles[i].getWidth();
									y = puzzles[i].getY();
									break;
								}
								case BOTTOM: {
									x = puzzles[i].getX();
									y = puzzles[i].getY()
											+ puzzles[i].getHeight();
									break;
								}
								case LEFT: {
									x = puzzles[i].getX()
											- puzzles[i].getWidth();
									y = puzzles[i].getY();
									break;
								}
								}
								if (getPuzzleAt(x, y) == null) {
									puzzles[maxelement].setLocation(x, y);
								}
							}
						}
//						puzzles[maxelement].setLocation(x, y);
						
					}
				}
				p.closeWindow();
			}
		}).start();

	}

	/* save buffered chunk image to file */
	protected boolean saveImageToFile(BufferedImage buffer,
			String chunkFileFormat, String filePathName) {
		FileOutputStream file = null;
		boolean executionFlag = true;
		try {
			file = new FileOutputStream(filePathName + "." + chunkFileFormat);
			ImageIO.write(buffer, chunkFileFormat, file);
		} catch (IOException e) {
			executionFlag = false;
			JOptionPane.showMessageDialog(null,
					"Cann't write file!\n" + e.getMessage(), "I/O Error",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				if (file != null) {
					file.close();
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"I/O error!\n" + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				executionFlag = false;
			}
		}
		return executionFlag;
	}

	/* create pazzles array */
	public void pazzlesArrayFromPath(String folderPath, Progressable p)
			throws IOException {
		if (puzzles != null) {
			puzzles = null;
		}
		File files = new File(folderPath);
		chunks = files.listFiles().length;
		puzzles = new Puzzle[chunks];
		p.setMaxprogress(chunks);
		for (int i = 0; i < chunks; i++) {
			BufferedImage bufferedImage = ImageIO.read(files.listFiles()[i]);
			puzzles[i] = new Puzzle(bufferedImage, i, this);
			p.setProgress(i);
		}

	}

	/* cut input image to chunks and save them to files */
	public void cutImage(final BufferedImage inputImage, final int rows, final int cols,
			final Progressable p) {
		chunks = rows + cols;
		p.setMaxprogress(chunks);
		chunkWidth = Math.round(inputImage.getWidth() / cols);
		chunkHeight = Math.round(inputImage.getHeight() / rows);

		new Thread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				for (int x = 0; x < rows; x++) {
					for (int y = 0; y < cols; y++) {
						BufferedImage bufimg = new BufferedImage(chunkWidth,
								chunkHeight, inputImage.getType());
						Graphics2D gr = bufimg.createGraphics();
						gr.drawImage(inputImage, 0, 0, chunkWidth, chunkHeight,
								chunkWidth * y, chunkHeight * x, chunkWidth * y
										+ chunkWidth, chunkHeight * x
										+ chunkHeight, null);
						gr.dispose();
						if (!saveImageToFile(bufimg, chunkImageFormat,
								chunksfolderPath + "\\" + chunkFileNamePrefix
										+ String.valueOf(x + "." + y))) {
							return;
						}
						count++;
						p.setProgress(count);
					}

				}
				p.closeWindow();
			}
		}).start();
	}

	public int getColorDistance() {
		return colorDistance;
	}

	public void setColorDistance(int colorDistance) {
		this.colorDistance = colorDistance;
	}

}
