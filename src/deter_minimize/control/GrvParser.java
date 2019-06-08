package deter_minimize.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import deter_minimize.model.Link;
import deter_minimize.model.State;
import deter_minimize.view.Frame;

public class GrvParser {
	public static String FOLDER = "graphs" + File.separator;

	public static void save(ArrayList<State> states, String filename) {
		try {
			FileWriter fw = new FileWriter(FOLDER + filename);
			BufferedWriter output = new BufferedWriter(fw);

			for (State s : states) {
				output.write(s.getName() + "[");
				output.write("x=" + Integer.toString(s.getPos().x)
						+ ", " + "y=" + Integer.toString(s.getPos().y)
						+ ", " + "initial=" + Boolean.toString(s.isInitial())
						+ ", " + "final=" + Boolean.toString(s.isFinal())
						+ "];");
				output.newLine();
			}
			for (State s : states) {
				for (Link l : s.getLinks()) {
					output.newLine();
					output.write(l.getStart().getName() + " --{"
							+ l.getTransition() + "}["
							+ "curve="+l.getCurve()+"]--> "
							+ l.getEnd().getName() + ";");
				}
			}
			output.flush();
			output.close();
		} catch (IOException ioe) {
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}
	}

	public static ArrayList<State> open(String filename) {
		ArrayList<State> ret = new ArrayList<State>();

		try {
			InputStream flux = new FileInputStream(FOLDER + filename);
			InputStreamReader lecture = new InputStreamReader(flux);
			BufferedReader buff = new BufferedReader(lecture);
			String ligne;
			int num = 0;
			while ((ligne = buff.readLine()) != null) {
				num++;
				Matcher m;
				if ((m = Pattern.compile(
						"([a-zA-Z0-9_,]+)"
								+ "\\["
								+ "x=([0-9]+), "
								+ "y=([0-9]+), "
								+ "initial=(true|false), "
								+ "final=(true|false)"
								+ "\\];")
						.matcher(ligne)).matches()) {
					ret.add(new State(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), 
							m.group(1), Boolean.parseBoolean(m.group(4)), Boolean.parseBoolean(m.group(5))));
				} else if ((m = Pattern.compile(
						"([a-zA-Z0-9_,]+)" 
								+ " \\-\\-\\{"
								+ "([a-zA-Z0-9:=/ ]+(,[a-zA-Z0-9:=/ ]+)*)"
								+ "\\}\\[curve=(\\-?[0-9]+)\\]\\-\\-\\>" + " ([a-zA-Z0-9_,]+);").matcher(
						ligne)).matches()) {
					State start = state(m.group(1), ret);
					State end = state(m.group(5), ret);
					if (start != null && end != null) {
						Link l = new Link(m.group(2), end);
						l.setCurve(Integer.parseInt(m.group(4)));
						start.addLink(l);
					} else {
						buff.close();
						throw new Exception("Un état de la ligne " + num
								+ " n'existe pas.");
					}
				}
			}
			Frame.newTitle(filename);
			buff.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private static State state(String s, ArrayList<State> states) {
		for (State st : states) {
			if (st.getName().equals(s)) {
				return st;
			}
		}
		return null;
	}
}
