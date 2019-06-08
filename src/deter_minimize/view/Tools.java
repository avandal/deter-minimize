package deter_minimize.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import deter_minimize.control.GrvParser;

public class Tools extends JToolBar {
	private static final long serialVersionUID = 1L;

	private Frame parent;

	public Tools(Frame parent) {
		super();
		this.parent = parent;

		this.setFloatable(false);

		this.addElement(new JButton(), "new", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveBeforeNew();
				newFile();
			}
		});
		this.addSeparator(new Dimension(2, 1));
		this.addElement(new JButton(), "save", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		this.addSeparator(new Dimension(2, 1));
		this.addElement(new JButton(), "open", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveBeforeNew();
				open();
			}
		});
		this.addSeparator();
		this.addElement(new JButton(), "help", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				help();
			}
		});
	}

	public void addElement(final JButton b, final String icon,
			ActionListener action) {
		b.setText("");
		b.setBorderPainted(false);
		b.setIcon(new ImageIcon("img/tb_" + icon + "_out.png"));
		b.setPreferredSize(new Dimension(32, 32));
		b.setMinimumSize(new Dimension(32, 32));
		b.setMaximumSize(new Dimension(32, 32));
		b.addActionListener(action);
		b.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				b.setIcon(new ImageIcon("img/tb_" + icon + "_in.png"));
			}

			public void mouseExited(MouseEvent e) {
				b.setIcon(new ImageIcon("img/tb_" + icon + "_out.png"));
			}
		});
		this.add(b);
	}
	
	public void saveBeforeNew() {
		if (parent.isModified()) {
			if (JOptionPane.showConfirmDialog(null,
					"Voulez-vous enregistrer le fichier en cours ? ",
					"Enregistrer", JOptionPane.YES_NO_OPTION) == 0) {
				save();
			}
		}
	}

	public void newFile() {
		getParent().getDraw().clear();
		getParent().getDraw().setFilename(null);
		JOptionPane.showMessageDialog(null, "Nouveau fichier");
		Frame.newTitle(null);
	}

	public void save() {
		String filename = getParent().getDraw().getFilename();
		if (filename == null) {
			filename = JOptionPane.showInputDialog(null,
					"Nom du nouveau fichier : ");
			if (filename != null && !filename.endsWith(".grv")) {
				filename += ".grv";
			}
		}
		if (filename != null
				&& JOptionPane.showConfirmDialog(null,
						"Êtes-vous sûr(e) de vouloir enregistrer cet automate dans "
								+ "le fichier " + GrvParser.FOLDER + filename,
						"Save file", JOptionPane.YES_NO_OPTION) == 0) {

			GrvParser.save(getParent().getDraw().getStates(), filename);
			JOptionPane.showMessageDialog(null,
					"L'automate a bien été enregistré dans le fichier "
							+ GrvParser.FOLDER + filename);
		} else {
			JOptionPane.showMessageDialog(null, "Abandon de la sauvegarde");
		}
	}

	public void open() {
		JFileChooser fc = new JFileChooser(GrvParser.FOLDER);
		fc.setFileFilter(new FileNameExtensionFilter("GRV graphs (.grv)", "grv"));
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			getParent().getDraw().open(fc.getSelectedFile().getName());
			JOptionPane.showMessageDialog(null, "Ouverture du fichier");
		} else {
			JOptionPane.showMessageDialog(null, "Abandon de l'ouverture");
		}
	}

	public void help() {
		JOptionPane
				.showMessageDialog(
						null,
						"- Créer un état : Clic droit dans une zone vide\n"
								+ "- Bouger un état : Clic gauche sur un état et déplacer la souris\n"
								+ "- Modifier un état : Clic gauche sur un état et menu de gauche (onglet 'States')\n"
								+ "- Créer une arête : Clic droit d'un état à un autre\n"
								+ "- Modifier une arête : Menu de gauche (onglet 'Links')",
						"Help", JOptionPane.INFORMATION_MESSAGE);
	}

	public Frame getParent() {
		return parent;
	}
}
