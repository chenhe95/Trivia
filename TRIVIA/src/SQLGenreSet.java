
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author He
 */
public class SQLGenreSet {

	private TreeSet<String> genres = null;

	public SQLGenreSet() {
		genres = new TreeSet<>();
	}

	public void addGenre(String genre) {
		genres.add(genre);
	}

	public boolean contains(String genre) {
		if (genres.contains(genre)) {
			return true;
		} else {
			for (String s : genres) {
				if (genre.matches("\\s*" + s + "\\s*") || s.matches("\\s*" + genre + "\\s*")) {
					return true;
				}
			}
			return false;
		}
	}

	public int size() {
		return genres.size();
	}

	public ArrayList<String> getList() {
		return new ArrayList<>(genres);
	}

	public ArrayList<String> getScrambledList() {
		ArrayList<String> list = new ArrayList<>(genres);
		Collections.shuffle(list);
		return list;
	}

	@Override
	public String toString() {
		String s = null;
		for (String genre : genres) {
			if (s == null) {
				s = genre;
			} else {
				s = s + ", " + genre;
			}
		}
		return s;
	}

	@Override
	public int hashCode() {
		return genres.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SQLGenreSet) {
			return genres.equals(((SQLGenreSet) o).genres);
		}
		return false;
	}
}
