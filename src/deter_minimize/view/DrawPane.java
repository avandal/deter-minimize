package deter_minimize.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import deter_minimize.control.Algorithms;
import deter_minimize.control.GrvParser;
import deter_minimize.model.Link;
import deter_minimize.model.State;
import deter_minimize.view.edit_link.LinkChooser;
import deter_minimize.view.edit_state.StateChooser;

public class DrawPane extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int size = Frame.HEIGHT;
	private static final int sizeSommet = 40;
	private static final double organize_constant = size / 4;

	private Frame parent;

	private ArrayList<State> states = new ArrayList<State>();
	private State selectedState = null;
	private Link selectedLink = null;
	private String click = "none";
	private boolean dragged = false;

	private int mousex = 0, mousey = 0;

	private String filename;

	public DrawPane(Frame parent, String filename) {
		this.parent = parent;
		this.filename = filename;
		this.setPreferredSize(new Dimension(size, size));

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON3) {
					click = "right";
					if (arg0.getX() != -1 && arg0.getY() != -1
							&& containsSommet(arg0.getX(), arg0.getY(), 30) == null) {
						State s = new StateChooser(arg0.getX(), arg0.getY())
								.showDialog();
						if (s != null) {
							if (!nameExists(s.getName())) {
								states.add(s);
								parent().reInit();
								parent().modified();
								repaint();
							} else {
								JOptionPane.showMessageDialog(null,
										"Ce nom existe déjà", "Erreur",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					} else {
						selectedState = containsSommet(arg0.getX(), arg0.getY(), 15);
					}
				} else if (arg0.getButton() == MouseEvent.BUTTON1) {
					click = "left";
					selectedState = containsSommet(arg0.getX(), arg0.getY(), 15);
					if (selectedState != null) {
						parent().getEditor().selectState(selectedState);
					} else {
						parent().reInit();
					}
				}
			}

			public void mouseReleased(MouseEvent arg0) {
				if (click.equals("right") && !dragged) {
					JOptionPane.showMessageDialog(null,
							"Impossible de créer un sommet ici", "erreur",
							JOptionPane.ERROR_MESSAGE);
				} else if (click.equals("right") && dragged) {
					State fin = containsSommet(arg0.getX(), arg0.getY(), 15);
					if (fin != null) {
						Link a = new LinkChooser(selectedState, fin).showDialog();
						if (a != null) {
							selectedState.addLink(a);
							parent().modified();
							parent().reInit();
						}
					}
				}
				click = "none";
				dragged = false;
				repaint();
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent arg0) {
				if (!dragged)
					dragged = true;
				if (click.equals("left")) {
					if (selectedState != null) {
						selectedState.setPos(arg0.getX(), arg0.getY());
						parent().modified();
						repaint();
					}
				} else if (click.equals("right")) {
					mousex = arg0.getX();
					mousey = arg0.getY();
					repaint();
				}
			}

			public void mouseMoved(MouseEvent arg0) {
			}
		});

		this.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getKeyCode()) {
				case KeyEvent.VK_O:
					organize();
					break;
				case KeyEvent.VK_D:
					determinize();
					break;
				case KeyEvent.VK_C:
					clear();
					break;
				default:
					break;
				}
				repaint();
			}

			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		if (filename != null && !filename.isEmpty()) {
			states.clear();
			states.addAll(GrvParser.open(filename));
			//organize();
		}
		repaint();
	}
	
	public void open(String filename) {
		if (filename != null && !filename.isEmpty()) {
			states.clear();
			states.addAll(GrvParser.open(filename));
			this.filename = filename;
			//organize();
			parent.reInit();
		}
	}

	public State containsSommet(int x, int y, int r) {
		for (State s : states) {
			if (Math.sqrt(Math.pow(s.getPos().x - x, 2)
					+ Math.pow(s.getPos().y - y, 2)) <= r)
				return s;
		}
		return null;
	}

	public boolean nameExists(String s) {
		for (State som : states) {
			if (som.getName().equals(s))
				return true;
		}
		return false;
	}

	public boolean nameExists(String s, State without) {
		for (State som : states) {
			if (som.getName().equals(s) && som != without)
				return true;
		}
		return false;
	}

	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, size, size);
		
		// Dessin d'une arête : Clic droit + souris bougée + pas d'état sélectionné
		if (click.equals("right") && dragged && selectedState != null) {
			g.setColor(Color.red);
			g.drawLine(selectedState.getPos().x, selectedState.getPos().y, mousex, mousey);
		}

		// Aretes
		for (State s : states) {
			// Choix de l'épaisseur des états si l'état en cours est sélectionné ou non
			if (selectedState == s) {
				((Graphics2D) g).setStroke(new BasicStroke(2));
			} else {
				((Graphics2D) g).setStroke(new BasicStroke(1));
			}
			g.setColor(Color.red);
			
			// Etat initial : Flèche de départ
			if (s.isInitial()) {
				g.drawLine(s.getPos().x - sizeSommet / 2, s.getPos().y,
						s.getPos().x - sizeSommet, s.getPos().y);
				g.drawLine(s.getPos().x - sizeSommet / 2, s.getPos().y,
						s.getPos().x - sizeSommet / 2 - 5, s.getPos().y - 5);
				g.drawLine(s.getPos().x - sizeSommet / 2, s.getPos().y,
						s.getPos().x - sizeSommet / 2 - 5, s.getPos().y + 5);
			}
			for (Link a : s.getLinks()) {
				if (a == selectedLink || s == selectedState || a.getEnd() == selectedState) {
					((Graphics2D) g).setStroke(new BasicStroke(2));
				} else {
					((Graphics2D) g).setStroke(new BasicStroke(1));
				}
				if (a.getStart() != a.getEnd()) {
					drawArete(g, a);
				} else {
					recursive(g, a);
				}
			}
		}

		// Sommets
		for (State s : states) {
			Color c = Color.black;
			if (s == selectedState) {
				((Graphics2D) g).setStroke(new BasicStroke(2));
			} else {
				((Graphics2D) g).setStroke(new BasicStroke(1));
			}
			int widthString = g.getFontMetrics().stringWidth(s.getName());
			int sizex = (widthString + 20 > sizeSommet) ? widthString + 20
					: sizeSommet;
			g.setColor(Color.white);
			g.fillOval(s.getPos().x - sizex / 2, s.getPos().y - sizeSommet / 2,
					sizex, sizeSommet);
			g.setColor(c);
			g.drawOval(s.getPos().x - sizex / 2, s.getPos().y - sizeSommet / 2,
					sizex, sizeSommet);
			
			// Etat final : double cercle
			if (s.isFinal()) {
				g.drawOval(
						s.getPos().x - (sizex - 2 * (sizex / 10)) / 2,
						s.getPos().y - (sizeSommet - 2 * (sizeSommet / 10)) / 2,
						sizex - 2 * (sizex / 10), sizeSommet - 2
								* (sizeSommet / 10));
			}
			g.drawString(s.getName(), s.getPos().x - widthString / 2,
					s.getPos().y + 5);
		}
	}

	public void drawArete(Graphics g, Link a) {
		// Positions des deux sommets
		int xa = a.getStart().getPos().x;
		int ya = a.getStart().getPos().y;
		int xb = a.getEnd().getPos().x;
		int yb = a.getEnd().getPos().y;
		// Distance entre A et B
		double d = Math.sqrt(Math.pow(xb - xa, 2) + Math.pow(yb - ya, 2));
		// Calcul du milieu
		int middlex = (int) (xa + d / 2);
		double r = Math.acos((xb - xa) / d);
		if (yb < ya)
			r = -r;

		((Graphics2D) g).rotate(r, xa, ya);

		// for (double t = Math.PI; t < Math.PI*2; t += Math.PI*2/100) {
		// g.drawRect((int)(middlex + d/2 * Math.cos(t)), (int)(ya + e *
		// Math.sin(t)), 1, 1);
		// }
		QuadCurve2D q = new QuadCurve2D.Float();
		q.setCurve(xa, ya, middlex, ya - 2 * a.getCurve(), xa + d, ya);
		((Graphics2D) g).draw(q);

		g.drawLine(middlex, ya - a.getCurve(), middlex - 10, ya - 10 - a.getCurve());
		g.drawLine(middlex, ya - a.getCurve(), middlex - 10, ya + 10 - a.getCurve());

		int temp = ya - a.getCurve ()- 3;
		((Graphics2D) g).rotate(-r, middlex, temp);
		g.setColor(Color.blue);
		g.drawString(a.getTransition(), middlex + 5, temp - 5);
		g.setColor(Color.red);
		((Graphics2D) g).rotate(r, middlex, temp);

		((Graphics2D) g).rotate(-r, xa, ya);
	}

	public void recursive(Graphics g, Link a) {
		int x = a.getStart().getPos().x;
		int y = a.getStart().getPos().y;

		g.drawOval(x - 20, y - 40, 40, 40);
		g.drawLine(x - 10, y - 50, x, y - 40);
		g.drawLine(x - 10, y - 30, x, y - 40);
		g.setColor(Color.blue);
		g.drawString(a.getTransition(), x + 5, y - 45);
		g.setColor(Color.red);
	}

	public void determinize() {
		ArrayList<State> test = null;
		try {
			test = Algorithms.determinize(states);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					e, "Erreur",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		if (test != null) {
			states.clear();
			states.addAll(test);
			organize();
			parent.modified();
			parent.reInit();
		}
	}

	public void minimize() {
		ArrayList<State> test = null;
		try {
			test = Algorithms.minimize(states);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					e, "Erreur",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		if (test != null) {
			states.clear();
			states.addAll(test);
			organize();
			parent.modified();
			parent.reInit();
		}
	}

	public void organize() {
		for (State s : states) {
			for (Link l : s.getLinks()) {
				l.setCurve(30);
			}
		}
		for (double t = 0, i = 0; t < Math.PI * 2 && i < states.size(); t += Math.PI
				* 2 / states.size(), i++) {
			states.get((int) i).setPos(
					(int) (size / 2 + organize_constant
							* Math.cos(t + 3 * Math.PI / 2)),
					(int) (size / 2 + organize_constant
							* Math.sin(t + 3 * Math.PI / 2)));
		}
	}

	public void rename() {
		for (int i = 0; i < states.size(); i++) {
			states.get(i).setName(Integer.toString(i));
		}
		parent.modified();
		parent.reInit();
	}

	public void clear() {
		states.clear();
		parent.modified();
		parent.reInit();
	}

	public State sommet(String a) {
		for (State s : states) {
			if (s.getName().equals(a)) {
				return s;
			}
		}
		return null;
	}

	public void deleteState(State som) {
		for (State s : states) {
			for (int i = 0; i < s.getLinks().size(); i++) {
				if (s.getLinks().get(i).getEnd() == som) {
					s.getLinks().remove(i);
				}
			}
		}
		states.remove(som);
	}

	public ArrayList<State> getStates() {
		return states;
	}

	public ArrayList<Link> getLinks() {
		ArrayList<Link> ret = new ArrayList<Link>();
		for (State s : states) {
			for (Link a : s.getLinks()) {
				ret.add(a);
			}
		}
		ret.sort(new Comparator<Link>() {
			public int compare(Link l1, Link l2) {
				int ret = l1.getStart().getName()
						.compareTo(l2.getStart().getName());
				if (ret == 0) {
					ret = l1.getEnd().getName()
							.compareTo(l2.getEnd().getName());
				}
				return ret;
			}
		});
		return ret;
	}

	public Frame parent() {
		return parent;
	}

	public DrawPane me() {
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void selectState(State s) {
		selectedState = s;
		repaint();
	}
	
	public void selectLink(Link l) {
		selectedLink = l;
		repaint();
	}
}
