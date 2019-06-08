package deter_minimize.view.edit_state;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import deter_minimize.model.Link;
import deter_minimize.model.State;

public class StatesEditPane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private EditState parent;
	
	private JLabel title;
	
	private JPanel pSommet;
	
	private JTextField tfNom = new JTextField();
	private JButton bNom = new JButton("Ok");
	
	private JRadioButton rbInitY = new JRadioButton("Oui");
	private JRadioButton rbInitN = new JRadioButton("Non");
	
	private JRadioButton rbFinY = new JRadioButton("Oui");
	private JRadioButton rbFinN = new JRadioButton("Non");
	
	private JButton bSuppr = new JButton("Supprimer");
	
	private ActionListener action;
	
	private boolean edited = false;
	
	public StatesEditPane(EditState parent) {
		this.parent = parent;
		this.setLayout(new BorderLayout());
		
		title = new JLabel("Sélectionner un sommet", JLabel.CENTER);
		this.add(title, BorderLayout.NORTH);
	}
	
	public StatesEditPane(EditState parent, final State s) {
		this(parent);
		
		title.setText("Modification du sommet "+s.getName());
		
		pSommet = new JPanel();
		pSommet.setLayout(new BoxLayout(pSommet, BoxLayout.Y_AXIS));
		
		JPanel pNom = new JPanel();
		pSommet.add(pNom);
		
		pNom.add(new JLabel("Nom :", JLabel.CENTER));
		
		tfNom.setColumns(5);
		tfNom.setText(s.getName());
		pNom.add(tfNom);
		
		bNom = new JButton("Ok");
		pNom.add(bNom);
		
		JPanel pInitial = new JPanel();
		pSommet.add(pInitial);
		
		JLabel lInitial = new JLabel("Etat initial ?");
		pInitial.add(lInitial);
		
		ButtonGroup bgInit = new ButtonGroup();
		bgInit.add(rbInitY);
		bgInit.add(rbInitN);
		rbInitN.setSelected(true);
		
		pInitial.add(rbInitY);
		pInitial.add(rbInitN);
		
		JPanel pFinal = new JPanel();
		pSommet.add(pFinal);
		
		JLabel lFinal = new JLabel("Etat final ?");
		pFinal.add(lFinal);
		
		ButtonGroup bgFin = new ButtonGroup();
		bgFin.add(rbFinY);
		bgFin.add(rbFinN);
		rbFinN.setSelected(true);
		
		pFinal.add(rbFinY);
		pFinal.add(rbFinN);
		
		if (s.isInitial()) {
			rbInitY.setSelected(true);
			rbInitN.setSelected(false);
		} else {
			rbInitY.setSelected(false);
			rbInitN.setSelected(true);
		}
		
		if (s.isFinal()) {
			rbFinY.setSelected(true);
			rbFinN.setSelected(false);
		} else {
			rbFinY.setSelected(false);
			rbFinN.setSelected(true);
		}
		
		for (final Link l : s.getLinks()) {
			JPanel pTo = new JPanel();
			JLabel lTo = new JLabel("=> "+l.getEnd().getName()+" : ");
			pTo.add(lTo);
			
			final JTextField tfTo = new JTextField();
			tfTo.setColumns(5);
			tfTo.setText(l.getTransition());
			pTo.add(tfTo);
			
			JButton bToOk = new JButton("Ok");
			bToOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					edited();
					s.getLinks().get(s.getLinks().indexOf(l)).setTranstion(tfTo.getText());
					getParent().getParent().getParent().modified();
					paint(s);
				}
			});
			pTo.add(bToOk);

			final JPanel pAll = new JPanel();
			pAll.setLayout(new GridLayout(2,1));
			
			JButton bToSuppr = new JButton("Suppr");
			bToSuppr.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					edited();
					s.getLinks().remove(s.getLinks().indexOf(l));
					pSommet.remove(pAll);
					getParent().getParent().getParent().modified();
					paint(s);
				}
			});
			pTo.add(bToSuppr);
			
			JPanel pGap = new JPanel();
			final JSlider slidGap = new JSlider(-100,100,l.getCurve());
			slidGap.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					edited();
					l.setCurve(slidGap.getValue());
					paint(s);
				}
			});
			pGap.add(new JLabel("Curve gap :", JLabel.CENTER));
			pGap.add(slidGap);
			
			pAll.add(pTo);
			pAll.add(pGap);
			
			pSommet.add(pAll);
		}
		
		pSommet.add(bSuppr);
		
		this.add(new JScrollPane(pSommet), BorderLayout.CENTER);
		
		for (Component c : pSommet.getComponents()) {
				((JComponent) c).setAlignmentX(Box.CENTER_ALIGNMENT);
		}
		
		action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == bNom) {
					if (!getParent().getParent().getParent().getDraw().nameExists(tfNom.getText(), s)) {
						edited();
						s.setName(tfNom.getText());
					} else {
						JOptionPane.showMessageDialog(null,
								"Ce nom existe déjà", "Erreur",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (e.getSource() == rbInitY || e.getSource() == rbInitN) {
					edited();
					s.setInitial(rbInitY.isSelected());
				} else if (e.getSource() == rbFinY || e.getSource() == rbFinN) {
					edited();
					s.setFinal(rbFinY.isSelected());
				} else if (e.getSource() == bSuppr) {
					edited();
					deleteState(s);
				}
				
				if (edited) {
					getParent().getParent().getParent().modified();
					paint(s);
				}
			}
		};
		
		bNom.addActionListener(action);
		rbInitY.addActionListener(action);
		rbInitN.addActionListener(action);
		rbFinY.addActionListener(action);
		rbFinN.addActionListener(action);
		bSuppr.addActionListener(action);
		
		this.add(new JScrollPane(pSommet), BorderLayout.CENTER);
	}
	
	public void paint(State s) {
		edited();
		title.setText("Modification du sommet "+s.getName());
		paint();
	}
	
	public void paint() {
		parent.getParent().getParent().repaint();
		parent.getParent().repaint();
		parent.repaint();
		repaint();
		parent.getParent().getParent().validate();
		parent.getParent().validate();
		parent.validate();
		validate();
		parent.getListPane().refresh();
	}
	
	public void deleteState(State s) {
		parent.getParent().getParent().getDraw().deleteState(s);
		parent.getParent().getParent().reInit();
	}
	
	private void edited() {
		edited = !edited;
	}
	
	public EditState getParent() {
		return parent;
	}
}
