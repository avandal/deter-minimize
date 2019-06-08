package deter_minimize.view.edit_link;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import deter_minimize.model.Link;
import deter_minimize.model.State;

public class LinkChooser extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField tf;
	
	private Link arete;

	public LinkChooser(final State s1, final State s2) {
		setTitle(s1.getName()+" -> "+s2.getName());
		getContentPane().setLayout(new BorderLayout());
		
		JPanel p = new JPanel();
		getContentPane().add(p, BorderLayout.NORTH);
		
		JLabel l = new JLabel("Donne :");
		p.add(l);
		
		tf = new JTextField();
		p.add(tf);
		tf.setColumns(10);
		
		JPanel pValider = new JPanel();
		getContentPane().add(pValider, BorderLayout.SOUTH);
		
		JButton bValider = new JButton("Valider");
		bValider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (tf.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Champ non complété", "Erreur", JOptionPane.ERROR_MESSAGE);
				} else {
					arete = new Link(tf.getText(), s2);
					dispose();
				}
			}
		});
		pValider.add(bValider);
		
		pack();
		
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public Link showDialog() {
		return arete;
	}

}
