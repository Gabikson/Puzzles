import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.Border;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private final int windowWidth = 600;
	private final int windowHeight = 600;
	private final String title = "Puzzles by Gaba B.";
	private final int defaultColorRange = 15;
	private PuzzleService puzzleService = new PuzzleService();

	private String folerPath;

	private JPanel mainLayout = new JPanel();
	private Desk drawdesk = new Desk(null);
	private JButton openImageButton = new JButton("Open image for cutting");
	private JButton openFolderButton = new JButton("Get folder with puzzles");
	private JButton previewButton = new JButton("Preview image");
	private JButton validButton = new JButton("Check order");
	private JButton buildButton = new JButton("Auto Build");
	private JButton rotateLeft = new JButton("Rotate left");
	private JButton rotateRight = new JButton("Rotate right");
	private JScrollBar colorRate = new JScrollBar(JScrollBar.HORIZONTAL);
	private JLabel colorRateValue = new JLabel(String.valueOf(defaultColorRange));
	private JPanel autoBuilPanel = new JPanel();
	private Border border = BorderFactory
			.createTitledBorder("Autobuild controls");

	private PreviewWindow previewWindow = new PreviewWindow("Image Preview");

	private File imageFile;
	private final int maxColorRange = 265;
	private final String CHUNK_FORMAT = "JPG";
	private int cols;
	private int rows;

	public GameWindow() {
		WindowInit();
	}

	public void WindowInit() {
		setTitle(title);
		setMaximizedBounds(getMaximizedBounds());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(windowWidth, windowHeight);
		this.setLocationRelativeTo(null);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.autoBuilPanel.setBorder(border);
		this.add(mainLayout, BorderLayout.SOUTH);
		setPreferredSize(new Dimension(0, 70));
		this.setBackground(Color.black);
		this.drawdesk.setBackground(Color.black);
		this.add(drawdesk);
		this.mainLayout.add(openImageButton);
		this.openImageButton.addActionListener(new ActionListener() {
			/*
			 * action : open image for cutting for small chunks and save them to
			 * folder
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				OpenImageDialog openImageDialog = new OpenImageDialog(
						GameWindow.this);
				BufferedImage img = null;
				if (openImageDialog.showOpenDialog() == OpenImageDialog.DIALOG_OK) {
					imageFile = openImageDialog.getFile();
					int chuks = openImageDialog.getChunksCount();
					folerPath = openImageDialog.getFolder().getPath();
					int rowscols = chuks % 2;
					rows = chuks / 2;
					cols = rows;
					if (rowscols > 0) {
						cols += rowscols;
					}
					try {
						img = ImageIO.read(imageFile);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null,
								"Reading image error!\n" + e.getMessage(),
								"Reading error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					WaitProgressDialog progressDialog = new WaitProgressDialog();
					File f = new File(folerPath);
					for (File file : f.listFiles()) {
						file.delete();
					}

					puzzleService.setChunksFolderPath(folerPath);
					puzzleService.setChunkImageFormat(CHUNK_FORMAT);
					puzzleService.cutImage(img, cols, rows, progressDialog);
					previewWindow.setImage(img);
				}
			}
		});

		this.mainLayout.add(openFolderButton);
		this.openFolderButton.addActionListener(new ActionListener() {
			/*
			 * open folder with puzzels
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					drawdesk.removeAll();
					drawdesk.repaint();
					final WaitProgressDialog progressDialog = new WaitProgressDialog();
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								puzzleService.pazzlesArrayFromPath(fc
										.getSelectedFile().getPath(),
										progressDialog);
							} catch (IOException e) {
								JOptionPane.showMessageDialog(
										null,
										"Reading image error!\n"
												+ e.getMessage(),
										"Reading error",
										JOptionPane.ERROR_MESSAGE);
							}
							Random r = new Random();
							int rndm_x = 0;
							int rndm_y = 0;
							int chunkWidth = puzzleService.getChunkWidth();
							int chunkHeight = puzzleService.getChunkHeight();
							for (int i = 0; i < puzzleService.getPuzzles().length; i++) {
								Puzzle puzzle = puzzleService.getPuzzles()[i];
								rndm_x = Math.abs(r.nextInt(drawdesk.getWidth())
										- chunkWidth - 50);
								rndm_y = Math.abs(r.nextInt(drawdesk
										.getHeight()) - chunkHeight - 50);
								puzzle.setLocation(rndm_x, rndm_y);
								drawdesk.add(puzzle);

							}
							progressDialog.closeWindow();
							drawdesk.updateUI();
						}
					}).start();

				}
			}
		});

		this.mainLayout.add(validButton);
		this.validButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (puzzleService.validation()) {
					JOptionPane.showMessageDialog(null,
							"Puzzle compiled correctly!", "You are master!",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane
							.showMessageDialog(null,
									"Puzzle compiled  with error!",
									"Check order of puzzle!",
									JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		this.mainLayout.add(previewButton);
		this.previewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (previewWindow.getImage() != null)
					previewWindow.setVisible(true);
			}
		});

		this.autoBuilPanel.add(buildButton);
		this.buildButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				WaitProgressDialog progressDialog = new WaitProgressDialog();
				puzzleService.build(progressDialog);
			}
		});
		// rotate left
		this.rotateLeft.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		this.mainLayout.add(rotateLeft);

		// rotate right
		this.rotateRight.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		this.mainLayout.add(rotateRight);
		this.colorRate.setMaximum(maxColorRange);
		this.colorRate.setMinimum(0);
		this.colorRate.setValue(defaultColorRange);
		this.colorRate.setPreferredSize(new Dimension(300, 20));
		this.colorRate.addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				puzzleService.setColorDistance(e.getValue());
				colorRateValue.setText(String.valueOf(e.getValue()));
			}
		});
		puzzleService.setColorDistance(defaultColorRange);
		this.autoBuilPanel.add(colorRate);
		this.autoBuilPanel.add(colorRateValue);
		this.mainLayout.add(autoBuilPanel, BorderLayout.NORTH);
		this.setVisible(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		System.exit(0);
	}

	private boolean validation() {
		// int startPos = puzzles[0].getY();
		// if (puzzles[0].getAngle() != 0) {
		// puzzles[0].setBad(true);
		// return false;
		// } else
		// puzzles[0].setBad(false);
		// int stepX = 1;
		// int stepY = 1;
		// for (int i = 1; i < puzzles.length; i++) {
		// if (puzzles[i].getAngle() == 0) {
		// int x = puzzles[i].getX();
		// int y = puzzles[i].getY();
		//
		// if (stepX++ != this.cols) {
		// if (!(y <= startPos + distFact & y >= startPos - distFact)) {
		// puzzles[i].setBad(true);
		// return false;
		// }
		// if (stepX > 1 & stepY > 1) {
		// if (!(x <= puzzles[i - 1].getX() + chunkWidth
		// + distFact & x >= puzzles[i - 1].getX()
		// + chunkWidth - distFact)) {
		// puzzles[i].setBad(true);
		// return false;
		// }
		// }
		// } else { // new row
		// if (!(y <= startPos + chunkHeight + distFact & y >= startPos
		// + chunkHeight - distFact)) {
		// puzzles[i].setBad(true);
		// return false;
		// }
		//
		// if (!(x <= puzzles[i - this.cols].getX() + distFact & x >= puzzles[i
		// - this.cols].getX()
		// - distFact)) {
		// puzzles[i].setBad(true);
		// return false;
		// }
		//
		// startPos = puzzles[i].getY();
		// stepX = 1;
		// stepY++;
		// }
		// puzzles[i].setBad(false);
		// } else {
		// puzzles[i].setBad(true);
		// return false;
		// }
		// }
		return true;
	}

	//
	private void build() {
		// int colsstep = 0;
		// puzzles[0].setLocation(0, 0);
		// puzzles[0].setAngle(0);
		// for (int i = 1; i < puzzles.length; i++) {
		// puzzles[i].setAngle(0);
		// if (++colsstep != this.cols) {
		// puzzles[i].setLocation(puzzles[i - 1].getX() + this.chunkWidth,
		// puzzles[i - 1].getY());
		// } else {
		// puzzles[i].setLocation(puzzles[i - this.cols].getX(), puzzles[i
		// - this.cols].getY()
		// + this.chunkHeight);
		// colsstep = 0;
		// }
		// }
	}
}
