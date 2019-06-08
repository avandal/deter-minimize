package deter_minimize.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Frame extends JFrame {
	private static final long serialVersionUID = 1L;

	public static final int HEIGHT = 500;

	public static final String title = "deter-mini(mi)ze";

	private static JLabel lTitle = new JLabel(title);

	private DrawPane draw;
	private Editor editor;

	private JPanel pTop;
	private Tools tools;

	private Point initial;

	public static void main(String[] args) {
		Frame f = new Frame();
		f.run();
	}

	public Frame() {
		this.setTitle(title);
		getContentPane().setLayout(new BorderLayout());

		draw = new DrawPane(this, "graph3.grv");
		editor = new Editor(this);

		top();

		getContentPane().add(pTop, BorderLayout.NORTH);
		getContentPane().add(editor, BorderLayout.WEST);
		getContentPane().add(draw, BorderLayout.CENTER);
	}

	public void top() {
		pTop = new JPanel();
		pTop.setLayout(new GridLayout(2, 1));
		pTop.setPreferredSize(new Dimension(HEIGHT, 80));

		JPanel pTitle = new JPanel();
		pTitle.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pTitle.setBackground(new Color(220, 220, 220));

		lTitle.setPreferredSize(new Dimension(HEIGHT - 30, 30));
		lTitle.setForeground(Color.RED);

		final JButton bQuit = new JButton();
		bQuit.setBorderPainted(false);
		bQuit.setIcon(new ImageIcon("img/close_out.png"));
		bQuit.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				tools.saveBeforeNew();
				dispose();
			}

			public void mouseEntered(MouseEvent e) {
				try {
					bQuit.setIcon(new ImageIcon(ImageIO.read(new File(
							"img/close_in.png"))));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			public void mouseExited(MouseEvent e) {
				try {
					bQuit.setIcon(new ImageIcon(ImageIO.read(new File(
							"img/close_out.png"))));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		bQuit.setPreferredSize(new Dimension(30, 30));

		pTitle.add(lTitle);
		pTitle.add(bQuit);

		pTitle.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent arg0) {
				// get location of Window
				int thisX = getLocation().x;
				int thisY = getLocation().y;

				// Determine how much the mouse moved since the initial click
				int xMoved = (thisX + arg0.getX()) - (thisX + initial.x);
				int yMoved = (thisY + arg0.getY()) - (thisY + initial.y);

				// Move window to this position
				int X = thisX + xMoved;
				int Y = thisY + yMoved;
				setLocation(X, Y);
			}
		});

		pTitle.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				initial = arg0.getPoint();
			}
		});

		tools = new Tools(this);

		pTop.add(pTitle);
		pTop.add(tools);
	}

	public void run() {
		this.setUndecorated(true);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		// GrvParser.save(draw.getStates(), "graph3.grv");
		// GrvParser.open("test.dot");
	}

	public void reInit() {
		draw.repaint();
		draw.validate();
		editor.repaint();
		editor.validate();
		editor.getLinkEditor().reInit();
		editor.getStateEditor().reInit();
	}
	
	public void modified() {
		if (!lTitle.equals(title) && !isModified()) {
			lTitle.setText(lTitle.getText()+"*");
			repaint();
			validate();
		}
	}
	
	public boolean isModified() {
		return lTitle.getText().equals(title) || lTitle.getText().endsWith("*");
	}

	public static void newTitle(String filename) {
		if (filename != null && !filename.isEmpty()) {
			lTitle.setText(title + " - " + filename);
		} else {
			lTitle.setText(title);
		}
	}

	public DrawPane getDraw() {
		return draw;
	}

	public Editor getEditor() {
		return editor;
	}

	public Frame me() {
		return this;
	}
}
