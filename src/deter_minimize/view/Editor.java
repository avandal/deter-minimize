package deter_minimize.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import deter_minimize.model.State;
import deter_minimize.view.edit_link.EditLink;
import deter_minimize.view.edit_state.EditState;

public class Editor extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 300;

	private Frame parent;

	private JTabbedPane tabs;

	private EditState stateEditor;
	private EditLink linkEditor;

	private JPanel actionPane;

	public Editor(Frame parent) {
		this.parent = parent;

		this.setPreferredSize(new Dimension(WIDTH, Frame.HEIGHT));

		tabs = new JTabbedPane();
		actionPane = new JPanel();

		initActionPane();

		stateEditor = new EditState(this);
		stateEditor.setPreferredSize(new Dimension(Editor.WIDTH,
				2 * Frame.HEIGHT / 3));
		linkEditor = new EditLink(this);
		linkEditor.setPreferredSize(new Dimension(Editor.WIDTH,
				2 * Frame.HEIGHT / 3));

		tabs.addTab("States", stateEditor);
		tabs.addTab("Links", linkEditor);
		
		tabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				getParent().getDraw().selectLink(null);
				getParent().getDraw().selectState(null);
			}
		});

		this.add(tabs);
		this.add(actionPane);
	}

	public void initActionPane() {
		actionPane = new JPanel();
		actionPane.setPreferredSize(new Dimension(Editor.WIDTH,
				Frame.HEIGHT / 3));

		JButton bOrganize = new JButton("Organize states");
		bOrganize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getParent().getDraw().organize();
				stateEditor.getEditPane().paint();
			}
		});
		actionPane.add(bOrganize);

		JButton bClear = new JButton("Clear the board");
		bClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getParent().getDraw().clear();
				stateEditor.getEditPane().paint();
				stateEditor.reInit();
			}
		});
		actionPane.add(bClear);

		JButton bDeterminize = new JButton("Determinize");
		bDeterminize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getParent().getDraw().determinize();
				stateEditor.getEditPane().paint();
				stateEditor.reInit();
			}
		});
		actionPane.add(bDeterminize);

		JButton bRename = new JButton("Rename all states");
		bRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getParent().getDraw().rename();
				stateEditor.getEditPane().paint();
				stateEditor.reInit();
			}
		});
		actionPane.add(bRename);

		JButton bMinimize = new JButton("Minimize");
		bMinimize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getParent().getDraw().minimize();
				stateEditor.getEditPane().paint();
				stateEditor.reInit();
			}
		});
		actionPane.add(bMinimize);
	}

	public void selectState(State s) {
		stateEditor.selectState(s);
	}

	public EditState getStateEditor() {
		return stateEditor;
	}

	public EditLink getLinkEditor() {
		return linkEditor;
	}

	public Frame getParent() {
		return parent;
	}
}
