package deter_minimize.view.edit_state;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import deter_minimize.model.Link;
import deter_minimize.model.State;
import deter_minimize.view.DrawPane;

import java.awt.Component;

public class StateChooser extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private State sommet;
	
	private JTextField tfNom;
	
	private JRadioButton rbInitY = new JRadioButton("Oui");
	private JRadioButton rbInitN = new JRadioButton("Non");
	
	private JRadioButton rbFinY = new JRadioButton("Oui");
	private JRadioButton rbFinN = new JRadioButton("Non");
	
	private JButton bValider = new JButton("Valider");
	
	private ActionListener actionlist;

	/**
	 * @wbp.parser.constructor
	 */
	public StateChooser(final int x, final int y) {
		setTitle("Nouveau sommet");
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel pNom = new JPanel();
		getContentPane().add(pNom);
		pNom.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JLabel lNom = new JLabel("Nom :");
		pNom.add(lNom);
		tfNom = new JTextField();
		pNom.add(tfNom);
		tfNom.setColumns(10);
		
		JPanel pInitial = new JPanel();
		getContentPane().add(pInitial);
		
		JLabel lInitial = new JLabel("Etat initial ?");
		pInitial.add(lInitial);
		
		ButtonGroup bgInit = new ButtonGroup();
		bgInit.add(rbInitY);
		bgInit.add(rbInitN);
		rbInitN.setSelected(true);
		
		pInitial.add(rbInitY);
		pInitial.add(rbInitN);
		
		JPanel pFinal = new JPanel();
		getContentPane().add(pFinal);
		
		JLabel lFinal = new JLabel("Etat final ?");
		pFinal.add(lFinal);
		
		ButtonGroup bgFin = new ButtonGroup();
		bgFin.add(rbFinY);
		bgFin.add(rbFinN);
		rbFinN.setSelected(true);
		
		pFinal.add(rbFinY);
		pFinal.add(rbFinN);
		
		bValider = new JButton("Valider");
		bValider.setAlignmentX(Component.CENTER_ALIGNMENT);
		getContentPane().add(bValider);
		actionlist = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (tfNom.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Champ non complété", "Erreur", JOptionPane.ERROR_MESSAGE);
				} else {
					sommet = new State(x, y, tfNom.getText(), rbInitY.isSelected(), rbFinY.isSelected());
					dispose();
				}
			}
		};
		bValider.addActionListener(actionlist);
	}
	
	public StateChooser(final DrawPane draw, final State s) {
		this(s.getPos().x, s.getPos().y);
		setTitle("Modifier "+s.getName());
		
		tfNom.setText(s.getName());
		
		if (s.isInitial()) {
			rbInitY.doClick();
		} else {
			rbInitN.doClick();
		}
		
		if (s.isFinal()) {
			rbFinY.doClick();
		} else {
			rbFinN.doClick();
		}
		
		getContentPane().remove(bValider);
		for (Link a : s.getLinks()) {
			JPanel pTo = new JPanel();
			JLabel lTo = new JLabel("=> "+a.getEnd().getName()+" : ");
			pTo.add(lTo);
			JTextField tfTo = new JTextField();
			tfTo.setColumns(10);
			tfTo.setText(a.getTransition());
			pTo.add(tfTo);
			getContentPane().add(pTo);
		}
		getContentPane().add(bValider);
		bValider.removeActionListener(actionlist);
		bValider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (tfNom.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Champ non complété", "Erreur", JOptionPane.ERROR_MESSAGE);
				} else {
					sommet = new State(s.getPos().x, s.getPos().y, tfNom.getText(), rbInitY.isSelected(), rbFinY.isSelected());
					for (int i = 3; i < getContentPane().getComponentCount()-1; i++) {
						JPanel p = (JPanel) getContentPane().getComponent(i);
						State s2 = draw.sommet(((JLabel)p.getComponent(0)).getText().substring(3).split(" ")[0]);
						String to = ((JTextField)p.getComponent(1)).getText();
						sommet.addLink(new Link(to, s2));
					}
					dispose();
				}
			}
		});
	}
	
	public State showDialog() {
		pack();
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
		return sommet;
	}

}
