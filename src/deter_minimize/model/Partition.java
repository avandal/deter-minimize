package deter_minimize.model;

import java.util.ArrayList;

public class Partition {
	private ArrayList<ArrayList<State>> groups;
	
	public Partition(){
		groups = new ArrayList<ArrayList<State>>();
	};
	
	public Partition(ArrayList<ArrayList<State>> groups) {
		this.groups = groups;
	}
	
	public String equals(Partition other) {
		if (groups.size() != other.groups.size()) {
			return "Error : nombre de groupes différents";
		}
		for (int i = 0; i < groups.size(); i++){
			if (groups.get(i).size() != other.groups.get(i).size()) {
				return "Error : deux mêmes groupes ont un nombre différent d'éléments";
			}
			for (int j = 0; j < groups.get(i).size(); j++) {
				State s1 = groups.get(i).get(j), s2 = other.groups.get(i).get(j);
				if (!s1.getName().equals(s2.getName()) || s1.isFinal() != s2.isFinal() || s1.isInitial() != s2.isInitial()) {
					return "Error : deux éléments d'un même groupe sont différents";
				}
			}
		}
		return null;
	}
	
	public ArrayList<ArrayList<State>> getGroups() {
		return groups;
	}
	
	public void addGroup(ArrayList<State> group) {
		groups.add(group);
	}
	
	public void addGroups(ArrayList<ArrayList<State>> groups) {
		this.groups.addAll(groups);
	}
	
	public void removeGroup(int i) {
		groups.remove(i);
	}
	
	public void addState(State s, int idGroup) {
		if (idGroup >= 0 && idGroup < groups.size()) {
			groups.get(idGroup).add(s);
		}
	}
	
	public int size() {
		return groups != null ? groups.size() : -1;
	}
	
	public boolean isNotMinimized() {
		for (ArrayList<State> g : groups) {
			if (g != null && g.size() != 1) {
				return true;
			}
		}
		return false;
	}
	
	public void changeTo(Partition newPart) {
		groups.clear();
		groups.addAll(newPart.groups);
	}
	
	public String toString() {
		String s = "";
		for (ArrayList<State> ar : groups) {
			for (State st : ar) {
				s += st;
			}
			s += " ";
		}
		return s;
	}
}
