package cis450_project_cleaning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Main {

	public static void main(String[] args) {
		try {
			//links
			FileReader readLink = new FileReader("links.csv");
			Map<Integer, Integer> midtoiid = new HashMap<Integer, Integer>();
			Map<Integer, Integer> iidtomid = new HashMap<Integer, Integer>();
			Map<Integer, Integer> tidtomid = new HashMap<Integer, Integer>();
			BufferedReader buffLink = new BufferedReader(readLink);
			String line;
			while((line=buffLink.readLine()) != null){
				Integer[] array = new Integer[3];
				String buff = line.substring(0, line.indexOf(","));
				array[0] = Integer.parseInt(buff);
				line = line.substring(line.indexOf(",")+1);
				buff = line.substring(0, line.indexOf(","));
				array[1] = Integer.parseInt(buff);
				buff = line.substring(line.indexOf(",")+1);
				if(!buff.isEmpty()){
					array[2] = Integer.parseInt(buff);
					tidtomid.put(array[2], array[0]);
				}
				midtoiid.put(array[0], array[1]);
				iidtomid.put(array[1], array[0]);
			}
			buffLink.close();
			readLink.close();
			//mid ratings
			FileReader readRatings = new FileReader("ratings.csv");
			BufferedReader buffRatings = new BufferedReader(readRatings);
			Map<Integer, Movie> moviemap = new HashMap<Integer, Movie>();
			while((line=buffRatings.readLine()) != null){
				line = line.substring(line.indexOf(",")+1);
				String buff = line.substring(0, line.indexOf(","));
				int mid = Integer.parseInt(buff);
				line = line.substring(line.indexOf(",")+1);
				buff = line.substring(0, line.indexOf(","));
				double rating = Double.parseDouble(buff);
				if(moviemap.containsKey(mid)){
					Movie m = moviemap.get(mid);
					m.rating_sum += rating;
					m.rating_count += 1;
					moviemap.put(mid, m);
				} else{
					Movie m = new Movie();
					m.mid = mid;
					m.rating_sum = rating;
					m.rating_count = 1;
					moviemap.put(mid, m);
				}
			}
			
			readRatings.close();
			buffRatings.close();
			
			//genre + name of movies
			FileReader readMovies = new FileReader("movies.csv");
			BufferedReader buffMovies = new BufferedReader(readMovies);
			File movie_genre = new File("movie_genre.csv");
			BufferedWriter mg_w = new BufferedWriter(new FileWriter(movie_genre));
			mg_w.append("mid,genre\n");
			while((line=buffMovies.readLine()) != null){
				String buff = line.substring(0, line.indexOf(","));
				int id = Integer.parseInt(buff);
				line = line.substring(line.indexOf(",")+1);
				buff = line.substring(0, line.indexOf(","));
				if(!moviemap.containsKey(id)){
					moviemap.put(id, new Movie());
				}
				moviemap.get(id).name = buff;
				line = line.substring(line.indexOf(",")+1);
				while(line.indexOf('|') != -1){
					buff = line.substring(0, line.indexOf('|'));
					mg_w.append(moviemap.get(id).name + "," + buff + "\n");
					line = line.substring(line.indexOf('|')+1);
				}
				if(!line.isEmpty()){
					mg_w.write(moviemap.get(id).name + "," + line + "\n");
				}
			}
			mg_w.close();
			buffMovies.close();

			//directors- rotten
			Gson gson = new Gson();
			FileReader readRTM = new FileReader("RottenTomatoes-MovieInfo");
			BufferedReader buffRTM = new BufferedReader(readRTM);
			while((line=buffRTM.readLine()) != null){
				RottenData rd = gson.fromJson(line, RottenData.class);
				if(rd.abridged_directors != null){
					Director director = rd.abridged_directors.get(0);
					int id = rd.alternate_ids.imdb;
					id = iidtomid.get(id);
					if(moviemap.containsKey(id)){
						moviemap.get(id).director = director.name;
					}
				}
			}
			
			readRTM.close();
			buffRTM.close();
						
			//acts
			FileReader readTMovie = new FileReader("TMDBMovieInfo");
			BufferedReader buffTMovies = new BufferedReader(readTMovie);
			File acts = new File("acts.csv");
			BufferedWriter a_w = new BufferedWriter(new FileWriter(acts));
			a_w.append("mid,aid\n");
			while((line=buffTMovies.readLine()) != null){
				TMovie movie = gson.fromJson(line, TMovie.class);
				if(tidtomid.containsKey(movie.id)){
					int id = tidtomid.get(movie.id);
					for(People p: movie.crew){
						a_w.append(id + "," + p.personId + "\n");
					}
				}
			}
			
			buffTMovies.close();
			a_w.close();
			readTMovie.close();
			
			//print movie
			File movie = new File("my_movies.csv");
			BufferedWriter mw = new BufferedWriter(new FileWriter(movie));
			mw.append("mid,name,rating,director\n");
			for(Map.Entry<Integer, Movie> entry: moviemap.entrySet()){
				Movie m = entry.getValue();
				double rating = m.rating_sum/m.rating_count;
				NumberFormat format = new DecimalFormat("#0.00");
				String director =  m.director == null? "" : m.director;
				mw.append(m.mid + "," + m.name + "," + format.format(rating) + "," + director + "\n");
			}
			
			mw.close();
			
			//actors
			File actors = new File("actors.csv");
			BufferedWriter aw = new BufferedWriter(new FileWriter(actors));
			FileReader readActors = new FileReader("TMDBPersonInfo");
			aw.append("aid,name,day_of_birth,day_of_death\n");
			BufferedReader buffActors = new BufferedReader(readActors);
			while((line=buffActors.readLine()) != null){
				Actor actor = gson.fromJson(line, Actor.class);
				if(actor.dayofbirth == null){
					actor.dayofbirth = "";
				}
				if(actor.dayofdeath == null){
					actor.dayofdeath = "";
				}
				if(actor.name==null){
					actor.name = "";
				}
				aw.append(actor.personId + "," + actor.name + "," + actor.dayofbirth + "," + actor.dayofdeath + "\n");
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
