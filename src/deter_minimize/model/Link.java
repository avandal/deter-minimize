package deter_minimize.model;

public class Link {
	private State start;
	private String transition;
	private State end;
	
	private int curve = 30;
	
	public Link(String transition, State fin) {
		this.transition = transition;
		this.end = fin;
	}
	
	public void setDebut(State debut) {
		this.start = debut;
	}
	public void setFin(State end) {
		this.end = end;
	}
	
	public State getStart() {
		return start;
	}
	public State getEnd() {
		return end;
	}
	public int getCurve() {
		return curve;
	}
	public void setCurve(int curve) {
		this.curve = curve;
	}
	public String getTransition() {
		return transition;
	}
	public void addTransition(String s) {
		transition += s;
	}
	public void setTranstion(String s) {
		transition = s;
	}
	
	public String string() {
		return start.getName()+" --"+transition+"-> "+end.getName();
	}
	
	public String toString() {
		return start.getName()+" ==> "+transition+" ==> "+end.getName();
	}
}
