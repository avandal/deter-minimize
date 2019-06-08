package deter_minimize.view.edit_link;

import java.awt.GridLayout;

import javax.swing.JPanel;

import deter_minimize.model.Link;
import deter_minimize.view.Editor;

public class EditLink extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Editor parent;
	
	private LinksListPane listPane;
	private LinksEditPane editPane;
	
	
	public EditLink(Editor parent) {
		this.parent = parent;
		
		this.setLayout(new GridLayout(2,1));
		
		listPane = new LinksListPane(this);
		editPane = new LinksEditPane(this);
		
		this.add(listPane);
		this.add(editPane);
	}
	
	public void selectLink(Link l) {
		parent.getParent().getDraw().selectLink(l);
		listPane.selectLink(l);
		
		remove(editPane);
		editPane = new LinksEditPane(this, l);
		add(editPane);
		validate();
		repaint();
	}
	
	public void reInit() {
		listPane.refresh(parent.getParent().getDraw().getLinks());
		
		remove(editPane);
		editPane = new LinksEditPane(this);
		add(editPane);
		validate();
		repaint();
	}
	
	public LinksListPane getListPane() {
		return listPane;
	}
	
	public LinksEditPane getEditPane() {
		return editPane;
	}
	
	public Editor getParent() {
		return parent;
	}
}
