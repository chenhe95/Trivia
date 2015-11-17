
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
