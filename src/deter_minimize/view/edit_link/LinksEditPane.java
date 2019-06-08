package deter_minimize.view.edit_link;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import deter_minimize.model.Link;
import deter_minimize.model.State;

public class LinksEditPane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private EditLink parent;
	
	private JLabel title;
	
	private JPanel pLink;
	
	private boolean edited;
	
	public LinksEditPane(EditLink parent) {
		this.parent = parent;
		
		this.setLayout(new BorderLayout());
		
		title = new JLabel("Sélectionner une transition", JLabel.CENTER);
		this.add(title, BorderLayout.NORTH);
	}
	
	public LinksEditPane(EditLink parent, final Link l) {
		this(parent);
		
		title.setText("Modification de la transition "+l);
		
		pLink = new JPanel();
		
		final JPanel pTo = new JPanel();
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
				l.setTranstion(tfTo.getText());
				paint(l);
				getParent().getParent().getParent().modified();
			}
		});
		pTo.add(bToOk);
		
		JButton bSuppr = new JButton("Suppr");
		bSuppr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				edited();
				l.getStart().getLinks().remove(l.getStart().getLinks().indexOf(l));
				getParent().getListPane().deleteLink(l);
				getParent().reInit();
				paint(l);
				getParent().getParent().getParent().modified();
			}
		});
		
		JPanel pGap = new JPanel();
		final JSlider slidGap = new JSlider(-100,100,l.getCurve());
		slidGap.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				edited();
				l.setCurve(slidGap.getValue());
				paint(l);
			}
		});
		pGap.add(new JLabel("Curve gap :", JLabel.CENTER));
		pGap.add(slidGap);
		
		pLink = new JPanel();
		pLink.setLayout(new GridLayout(3,1));
		pLink.add(pTo);
		pLink.add(pGap);
		
		JPanel pSuppr = new JPanel();
		pSuppr.add(bSuppr);
		pLink.add(pSuppr);
		
		this.add(pLink, BorderLayout.CENTER);
	}
	
	public void paint(Link l) {
		edited();
		title.setText("Modification de la transition "+l);
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
		parent.reInit();
	}
	
	private void edited() {
		edited = !edited;
	}
	
	public EditLink getParent() {
		return parent;
	}
}
