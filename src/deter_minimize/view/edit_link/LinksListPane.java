package deter_minimize.view.edit_link;

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

import deter_minimize.model.Link;

public class LinksListPane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private EditLink parent;
	
	private JList<Link> list;
	private ListModel<Link> model = new DefaultListModel<Link>();
	
	public LinksListPane(EditLink parent) {
		this.parent = parent;
		
		this.setLayout(new BorderLayout());
		
		list = new JList<Link>(model);
		
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (list.getSelectedValue() != null)
					getParent().selectLink(list.getSelectedValue());
			}
		});

		for (Link l : parent.getParent().getParent().getDraw().getLinks()) {
			((DefaultListModel<Link>) model).addElement(l);
		}
		((DefaultListCellRenderer)list.getCellRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		
		this.add(new JLabel("Liste des transitions", JLabel.CENTER), BorderLayout.NORTH);
		this.add(new JScrollPane(list), BorderLayout.CENTER);
	}
	
	public void refresh() {
		list.repaint();
	}
	
	public void refresh(ArrayList<Link> links) {
		((DefaultListModel<Link>) model).removeAllElements();
		for (Link l : links) {
			((DefaultListModel<Link>) model).addElement(l);
		}
		refresh();
	}
	
	public void selectLink(Link l) {
		parent.getParent().getParent().getDraw().selectLink(l);
		list.setSelectedValue(l, true);
	}
	
	public void deleteLink(Link l) {
		((DefaultListModel<Link>) model).removeElement(l);
	}
	
	public EditLink getParent() {
		return parent;
	}
}
