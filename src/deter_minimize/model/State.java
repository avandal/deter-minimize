package deter_minimize.model;

import java.awt.Point;
import java.util.ArrayList;

public class State {
	private int x, y;
	
	private String name = "";
	private ArrayList<Link> links = new ArrayList<Link>();

	private boolean initial_state = false;
	private boolean final_state = false;
	
	public State(String name, boolean initial_state, boolean final_state) {
		this.name = name;
		this.initial_state = initial_state;
		this.final_state = final_state;
	}
	
	public State(int x, int y, String name, boolean initial_state, boolean final_state) {
		this(name, initial_state, final_state);
		this.x = x;
		this.y = y;
	}
	
	public Point getPos() {
		return new Point(x,y);
	}
	
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isInitial() {
		return initial_state;
	}
	public void setInitial(boolean init) {
		initial_state = init;
	}
	
	public boolean isFinal() {
		return final_state;
	}
	public void setFinal(boolean fin) {
		final_state = fin;
	}
	
	public void addLink(Link a) {
		if (a.getEnd() != null) {
			boolean exists = false;
			for (Link ar : links) {
				if (ar.getEnd() == a.getEnd()) {
					exists = true;
					for (String s : a.getTransition().split(",")) {
						if (!ar.getTransition().contains(s)) {
							ar.addTransition("," + s);
						}
					}
				}
			}
			if (!exists) {
				links.add(a);
				a.setDebut(this);
			}
		}
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}
	
	public String toString() {
		return name;
	}
}
