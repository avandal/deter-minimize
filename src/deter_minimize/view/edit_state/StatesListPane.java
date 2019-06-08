package deter_minimize.view.edit_state;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import deter_minimize.model.State;

public class StatesListPane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private EditState parent;
	
	private JList<State> list;
	private ListModel<State> model = new DefaultListModel<State>();
	
	public StatesListPane(EditState parent) {
		this.parent = parent;
		
		this.setLayout(new BorderLayout());
		
		list = new JList<State>(model);
		
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (list.getSelectedValue() != null)
					getParent().selectState(list.getSelectedValue());
			}
		});

		for (State s : parent.getParent().getParent().getDraw().getStates()) {
			((DefaultListModel<State>) model).addElement(s);
		}
		((DefaultListCellRenderer)list.getCellRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		
		this.add(new JLabel("Liste des sommets", JLabel.CENTER), BorderLayout.NORTH);
		this.add(new JScrollPane(list), BorderLayout.CENTER);
	}
	
	public void refresh() {
		list.repaint();
	}
	
	public void refresh(ArrayList<State> states) {
		((DefaultListModel<State>) model).removeAllElements();
		for (State s : states) {
			((DefaultListModel<State>) model).addElement(s);
		}
		refresh();
	}
	
	public void selectState(State s) {
		parent.getParent().getParent().getDraw().selectState(s);
		list.setSelectedValue(s, true);
	}
	
	public EditState getParent() {
		return parent;
	}
}
