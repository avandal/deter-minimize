package deter_minimize.view.edit_state;

import java.awt.GridLayout;

import javax.swing.JPanel;

import deter_minimize.model.State;
import deter_minimize.view.Editor;

public class EditState extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Editor parent;
	
	private StatesListPane listPane;
	private StatesEditPane editPane;
	
	
	public EditState(Editor parent) {
		this.parent = parent;
		
		this.setLayout(new GridLayout(2,1));
		
		listPane = new StatesListPane(this);
		editPane = new StatesEditPane(this);
		
		this.add(listPane);
		this.add(editPane);
	}
	
	public void selectState(State s) {
		parent.getParent().getDraw().selectState(s);
		listPane.selectState(s);
		
		remove(editPane);
		editPane = new StatesEditPane(this, s);
		add(editPane);
		validate();
		repaint();
	}
	
	public void reInit() {
		listPane.refresh(parent.getParent().getDraw().getStates());
		
		remove(editPane);
		editPane = new StatesEditPane(this);
		add(editPane);
		validate();
		repaint();
	}
	
	public StatesListPane getListPane() {
		return listPane;
	}
	
	public StatesEditPane getEditPane() {
		return editPane;
	}
	
	public Editor getParent() {
		return parent;
	}
}
