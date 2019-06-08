package deter_minimize.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import deter_minimize.model.Link;
import deter_minimize.model.Partition;
import deter_minimize.model.State;

public class Algorithms {

	// ===================================================================================================
	// ===================================================================================================
	// =========================================== DETERMINIZE
	// ===========================================
	// ===================================================================================================
	// ===================================================================================================

	/**
	 * Prend un automate fini en entrée et retourne ce même automate déterminisé
	 * 
	 * @param states
	 *            Automate non déterministe
	 * @return Automate déterministe
	 * @throws Exception
	 *             L'automate contient 0 ou 2+ états initi(al/aux)
	 */
	public static ArrayList<State> determinize(ArrayList<State> states)
			throws Exception {
		// On part de l'état initial
		int nbInit = nbInitial(states);
		if (nbInit == 0 || nbInit > 1) {
			throw new Exception("Il faut un (et un seul) état initial");
		}
		State init = initial(states);

		// On détermine l'alphabet
		String[] alpha = alphabet(states);

		// On va stocker les noms de tous les groupes (ArrayList) d'états dans
		// une ArrayList
		ArrayList<ArrayList<String>> tab = new ArrayList<ArrayList<String>>();

		// Contiendra l'automate déterminisé
		ArrayList<State> determinized = new ArrayList<State>();

		// L'automate déterministe commence par l'état initial de l'automate à
		// déterminisé
		determinized.add(init);
		ArrayList<State> nouv = new ArrayList<State>();

		// Pour chaque nouvel état trouvé
		for (int i = 0; i < determinized.size(); i++) {
			// On prévoit un nouveau groupe d'états
			tab.add(new ArrayList<String>());

			// Pour chaque lettre de l'alphabet
			for (String st : alpha) {

				// On trouve l'ensemble des états d'arrivée dont la transition
				// correspond à la lettre (fonction pricipale)
				State s = mix(transitionPar(st,
						oneToSome(determinized.get(i), states)));

				// Et on l'ajoute à un groupe temporaire
				nouv.add(s);

				// On stocke aussi son nom dans le groupe d'ensembles d'états
				tab.get(i).add((s == null) ? "" : s.getName());
			}
			// Et pour chaque état trouvé, s'il n'existe pas déjà, on l'ajoute à
			// l'automate final
			for (State s : nouv) {
				if (s != null && !contains(s, determinized)) {
					determinized.add(s);
				}
			}
		}

		// Une fois l'automate final trouvé, on ajoute les transitions
		for (int i = 0; i < determinized.size(); i++) {
			State s = determinized.get(i);
			for (int j = 0; j < alpha.length; j++) {
				String l = alpha[j];
				s.addLink(new Link(l, get(tab.get(i).get(j), determinized)));
			}
		}

		// Pour les états qui sont composés de plusieurs états (genre l'état
		// "1;2"), on remplace le(s) ';' par ','
		for (State s : determinized) {
			s.setName(s.getName().replaceAll(";", ","));
		}

		// Enfin on retourne l'automate déterminisé
		return determinized;
	}

	/**
	 * 
	 * @param sommets
	 *            automate
	 * @return nombre d'états initiaux
	 */
	private static int nbInitial(ArrayList<State> sommets) {
		int ret = 0;
		for (State s : sommets) {
			if (s.isInitial()) {
				ret++;
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param sommets
	 *            automate
	 * @return une copie du premier état initial trouvé (sans les transitions)
	 */
	private static State initial(ArrayList<State> sommets) {
		for (State s : sommets) {
			if (s.isInitial()) {
				return new State(s.getName(), s.isInitial(), s.isFinal());
			}
		}
		return null;
	}

	/**
	 * 
	 * @param sommets
	 *            automate
	 * @return alphabet lu par cet automate
	 */
	private static String[] alphabet(ArrayList<State> sommets) {
		ArrayList<String> retTemp = new ArrayList<String>();
		for (State s : sommets) {
			for (Link a : s.getLinks()) {
				for (String st : a.getTransition().split(",")) {
					if (!retTemp.contains(st)) {
						retTemp.add(st);
					}
				}
			}
		}
		String[] ret = new String[retTemp.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = retTemp.get(i);
		}
		return ret;
	}

	/**
	 * Pour un groupe d'états et une lettre donnée, on cherche pour chaque état
	 * A les états B par lesquels A passent par la lettre
	 * 
	 * @param lettre
	 *            lettre avec laquelle on passe
	 * @param sommets
	 *            états pour lesquels on veut connaître les états cibles par la
	 *            lettre
	 * @return états par lesquels on passe par la lettre choisie
	 */
	private static ArrayList<State> transitionPar(String lettre,
			ArrayList<State> sommets) {
		ArrayList<State> ret = new ArrayList<State>();
		for (State s : sommets) {
			for (Link a : s.getLinks()) {
				if (a.getTransition().contains(lettre)) {
					State som = new State(a.getEnd().getName(), a.getEnd()
							.isInitial(), a.getEnd().isFinal());
					if (!contains(som, ret))
						ret.add(som);
				}
			}
		}
		return uniques(ret);
	}

	/**
	 * Pour un état donné, retourne l'ensemble des états qui le composent (ex :
	 * (1,2) -> [(1),(2)])
	 * 
	 * @param s
	 * @param sommets
	 * @return
	 */
	private static ArrayList<State> oneToSome(State s, ArrayList<State> sommets) {
		ArrayList<State> ret = new ArrayList<State>();
		for (String st : s.getName().split(";")) {
			ret.add(get(st, sommets));
		}
		return ret;
	}

	/**
	 * Rassemble plusieurs états donnés
	 * 
	 * @param toMix
	 *            états à rassembler
	 * @return unique état composé des états donnés
	 */
	private static State mix(ArrayList<State> toMix) {
		if (toMix.size() == 0)
			return null;

		String nom = toMix.get(0).getName();
		for (int i = 1; i < toMix.size(); i++) {
			nom += ";" + toMix.get(i).getName();
		}
		State ret = new State(nom, false, etat_final(toMix));
		return sort(ret);
	}

	/**
	 * Réorganise le nom d'un état (ex : (1,4,2,0) -> (0,1,2,4))
	 * 
	 * @param s
	 *            état
	 * @return état dont le nom est réorganisé
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static State sort(State s) {
		if (s.getName().length() <= 1) {
			return s;
		}
		String[] ss = s.getName().split(";");
		Arrays.sort(ss, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return ((String) arg0).compareTo((String) arg1);
			}

		});
		String str = "";
		for (String st : ss) {
			str += ";" + st;
		}
		str = str.substring(1);
		s.setName(str);
		return s;
	}

	/**
	 * 
	 * @param sommets
	 *            ensemble d'états
	 * @return vrai si l'un des états est final
	 */
	private static boolean etat_final(ArrayList<State> sommets) {
		for (State s : sommets) {
			if (s.isFinal()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param s
	 *            état à chercher
	 * @param sommets
	 *            ensemble d'états
	 * @return vrai si l'ensemble d'états donné contient l'état donné
	 */
	private static boolean contains(State s, ArrayList<State> sommets) {
		for (State som : sommets) {
			if (s.getName().equals(som.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Recherche un sommet par son nom
	 * 
	 * @param st
	 *            nom de l'état à trouver
	 * @param sommets
	 *            ensemble d'états
	 * @return état dont le nom est donné en paramètres, null sinon
	 */
	private static State get(String st, ArrayList<State> sommets) {
		for (State som : sommets) {
			if (st.equals(som.getName())) {
				return som;
			}
		}
		return null;
	}

	/**
	 * Retire tous les doublons d'un ensemble d'états
	 * 
	 * @param sommets
	 *            ensemble d'états
	 * @return ensemble d'états trié
	 */
	public static ArrayList<State> uniques(ArrayList<State> sommets) {
		ArrayList<Integer> index = new ArrayList<Integer>();
		for (int i = 0; i < sommets.size(); i++) {
			boolean doublon = false;
			int j = i;
			while (!doublon && ++j < sommets.size()) {
				if (sommets.get(i).getName().equals(sommets.get(j).getName())) {
					doublon = true;
					index.add(j);
				}
			}
		}
		for (int i = index.size() - 1; i >= 0; i--) {
			sommets.remove(index.get(i));
		}
		return sommets;
	}

	// ====================================================================================================
	// ====================================================================================================
	// ============================================= MINIMIZE
	// =============================================
	// ====================================================================================================
	// ====================================================================================================

	public static ArrayList<State> minimize(ArrayList<State> states)
			throws Exception {
		ArrayList<ArrayList<State>> group = init(states);
		if (group == null) {
			throw new Exception("Argument must be not null and not empty");
		}

		String[] alpha = alphabet(states);
		Partition part = new Partition(group);
		Partition[] partitions = new Partition[alpha.length];
		for (int i = 0; i < partitions.length; i++) {
			partitions[i] = new Partition(new ArrayList<ArrayList<State>>());
		}

		do {
			int i = -1;
			while (++i < partitions.length && part.isNotMinimized()) {
				part.changeTo(step(part, alpha[i]));
				partitions[i].changeTo(part);
			}
		} while (!samePartitions(partitions) && part.isNotMinimized());
		ArrayList<State> ret = new ArrayList<State>();
		for (ArrayList<State> ar : part.getGroups()) {
			if (ar.size() == 1) {
				ret.add(ar.get(0));
			} else {
				ret.add(mix_minimize(ar));
			}
		}
		for (State s : ret) {
			if (s.getLinks().size() == 0) {
				State st = oneToSome_minimize(s, states).get(0);
				for (String lettre : alpha) {
					for (Link l : st.getLinks()) {
						if (l.getTransition().contains(lettre)) {
							s.addLink(new Link(lettre, contained(l.getEnd(),
									ret)));
						}
					}
				}
			} else {
				for (Link l : s.getLinks()) {
					if (!ret.contains(l.getEnd())) {
						l.setFin(contained(l.getEnd(), ret));
					}
				}
			}
		}
		return ret;
	}

	private static State contained(State s, ArrayList<State> group) {
		for (State st : group) {
			for (String str : st.getName().split(",")) {
				if (str.equals(s.getName())) {
					return st;
				}
			}
		}
		return null;
	}

	private static ArrayList<ArrayList<State>> init(ArrayList<State> states) {
		ArrayList<ArrayList<State>> ret = new ArrayList<ArrayList<State>>();
		ret.add(new ArrayList<State>());
		ret.add(new ArrayList<State>());
		for (State s : states) {
			if (s.isFinal()) {
				ret.get(1).add(s);
			} else {
				ret.get(0).add(s);
			}
		}
		return ret;
	}

	private static boolean samePartitions(Partition[] partitions) {
		if (partitions[0] == null || partitions[0].size() <= 0) {
			System.out.println("Une des partitions est nulle");
			return false;
		}
		for (int i = 1; i < partitions.length; i++) {
			if (partitions[i] == null)
				return false;
			String ret = partitions[i - 1].equals(partitions[i]);
			if (ret != null) {
				// System.out.println(ret);
				return false;
			}
		}
		return true;
	}

	private static Partition step(Partition prec, String lettre) {
		Partition ret = new Partition();
		for (ArrayList<State> ar : prec.getGroups()) {
			ArrayList<State> poubelle = new ArrayList<State>();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			if (ar.size() == 1) {
				ret.addGroup(ar);
			} else {
				for (int i = 0; i < ar.size(); i++) {
					State s = ar.get(i);
					if (!isIn(lettre, s, ar)) {
						poubelle.add(s);
						ids.add(i);
					}
				}
				for (int i = ids.size() - 1; i >= 0; i--) {
					ar.remove((int) (ids.get(i)));
				}
				ret.addGroup(ar);
				ret.addGroups(poubelle(poubelle, lettre,
						except(ar, prec.getGroups())));
			}
		}
		for (int i = 0; i < ret.size(); i++) {
			if (ret.getGroups().get(i).size() == 0) {
				ret.removeGroup(i);
				i--;
			}
		}
		return ret;
	}

	private static boolean isIn(String lettre, State s, ArrayList<State> group) {
		for (State st : group) {
			for (Link l : s.getLinks()) {
				if (l.getTransition().contains(lettre) && l.getEnd() == st) {
					return true;
				}
			}
		}
		return false;
	}

	private static ArrayList<ArrayList<State>> except(ArrayList<State> ar,
			ArrayList<ArrayList<State>> group) {
		ArrayList<ArrayList<State>> ret = new ArrayList<ArrayList<State>>();
		for (ArrayList<State> g : group) {
			if (g != ar) {
				ret.add(g);
			}
		}
		return ret;
	}

	private static ArrayList<ArrayList<State>> poubelle(
			ArrayList<State> poubelle, String lettre,
			ArrayList<ArrayList<State>> groups) {
		ArrayList<ArrayList<State>> ret = new ArrayList<ArrayList<State>>();
		for (ArrayList<State> ar : groups) {
			ArrayList<State> nouv = new ArrayList<State>();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (int i = 0; i < poubelle.size(); i++) {
				if (isIn(lettre, poubelle.get(i), ar)) {
					nouv.add(poubelle.get(i));
					ids.add(i);
				}
			}
			for (int i = ids.size() - 1; i >= 0; i--) {
				poubelle.remove((int) (ids.get(i)));
			}
			ret.add(nouv);
		}
		for (State s : poubelle) {
			ret.add(new ArrayList<State>());
			ret.get(ret.size() - 1).add(s);
		}
		return ret;
	}

	private static State mix_minimize(ArrayList<State> toMix) {
		if (toMix.size() == 0)
			return null;

		String nom = toMix.get(0).getName();
		for (int i = 1; i < toMix.size(); i++) {
			nom += "," + toMix.get(i).getName();
		}
		State ret = new State(nom, false, etat_final(toMix));
		return sort(ret);
	}

	private static ArrayList<State> oneToSome_minimize(State s,
			ArrayList<State> sommets) {
		ArrayList<State> ret = new ArrayList<State>();
		for (String st : s.getName().split(",")) {
			ret.add(get(st, sommets));
		}
		return ret;
	}
}
