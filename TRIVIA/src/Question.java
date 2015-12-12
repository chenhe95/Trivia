
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author He
 */
public class Question {

	private String question;
	private List<String> choices;
	private int answer;

	private static final int questionTypeN = 10;
	private static final int choiceLimit = 5; // maximum number of choices
	// the resulting query must have at least this many valid tuples
	private static final int resultThreshold = 20;
	// 3% chance to generate fresh data on question generation
	private static double refreshThreshold = 0.03;

	private static final String[] ALL_GENRES = { "Adventure", "Animation", "Children", "Comedy", "Fantasy", "Action",
			"Drama", "Romance", "Crime", "Thriller", "IMAX", "Documentary", "Film-Noir", "Sci-Fi", "War", "Horror",
			"Western", "Fantasy", "Mystery", "Musical", "Animation" };

	@SuppressWarnings("rawtypes")
	private static final HashMap<Integer, QuestionSetWrapper> questionCache = new HashMap<>();

	private Question(String question, List<String> choices, int answer) {
		this.question = question;
		this.choices = choices;
		this.answer = answer;
	}

	public List<String> getChoices() {
		return choices;
	}

	public int getAnswer() {
		return answer;
	}

	/**
	 * Returns the appropriate rating given the difficulty
	 *
	 *
	 *
	 * @param difficulty
	 * @return
	 */
	private static double getRating(int difficulty) {
		switch (difficulty) {
		case 0:
			return 4;
		case 1:
			return 3;
		case 2:
			return 2;
		case 3:
		default:
			return 0;
		}
	}

	public String getQuestion() {
		return question;
	}

	public static List<Question> generateQuestionSet(int difficulty, int questionSetSize) {
		List<Question> questionList = new ArrayList<>(questionSetSize);
		for (int i = 0; i < questionSetSize; ++i) {
			try {
				Question q = generateQuestion(difficulty, 4 * questionSetSize, true);
				q.question = q.getQuestion().replaceAll("\\s\\s", " ");
				q.question = q.getQuestion().replaceAll("\\(\\s", "(");
				q.question = q.getQuestion().replaceAll("\\s\\)", ")");
				q.question = q.getQuestion().replaceAll("\\s\\?", "?");
				q.question = q.getQuestion().replaceAll("\\s\\.", ".");
				for (int j = 0; j < q.getChoices().size(); ++j) {
					q.getChoices().set(j, q.getChoices().get(j).trim());
				}
				questionList.add(q);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return questionList;
	}

	private static boolean cacheContainsKey(int key, int difficulty) {
		return questionCache.containsKey(getCacheKey(key, difficulty));
	}

	private static void loadIndex(int qType, int difficulty, int choiceSize, boolean skipElements) throws SQLException {
		ArrayList<String> actorNames = new ArrayList<>();
		ArrayList<String> actorDOBs = new ArrayList<>();
		ArrayList<String> movies = new ArrayList<>();
		ArrayList<Integer> years = new ArrayList<>();
		ArrayList<Integer> idList = new ArrayList<>();
		int skip = maxSkipNumber(choiceSize, difficulty) / 2;
		if (!skipElements) {
			skip = 2;
		} else {
			skip += (int) (Math.random() * skip);
			skip = skip * 3 / 4;
		}
		switch (qType) {
		case 0:
			Connection connection = DBConnect.getConnection();

			ResultSet rs = null;
			try {
				String qry = "SELECT * from (SELECT @row := @row +1 AS rownum, name, "
						+ "rating FROM (SELECT @row := 0) r, movie.movies offset " + ") ranked WHERE rownum % " + skip
						+ " = 1 AND rating > " + getRating(difficulty);
				qry = "select M.name as name from movie.movies as M where M.mid % " + skip + " = 1 and M.rating > "
						+ getRating(difficulty);

				Statement s = connection.createStatement();
				System.out.println("Executing: " + qry);
				rs = s.executeQuery(qry);

				// rs.next() is actually very slow so we would
				// like to minimize the time by caching the results
				while (rs.next() && movies.size() <= choiceSize) {
					String name = "";
					int year = 0;
					String nameYear = rs.getString("name");
					System.out.println("Processing: " + nameYear);
					String tokens[] = nameYear.split("\\s");
					Pattern p = Pattern.compile("\\(\\d\\d\\d\\d\\)");
					for (String token : tokens) {
						Matcher m = p.matcher(token);
						if (m.matches()) {
							token = token.replaceAll("[\\(\\)]", "");
							try {
								year = Integer.parseInt(token);
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						} else {
							name = name + " " + token;
						}
					}
					if (year > 0 && !name.matches("(\\s)*")) {
						// from this, we guarantee that the indexes will
						// match
						// for (movie, year)
						movies.add(name);
						years.add(year);
					}
				}
				questionCache.put(getCacheKey(0, difficulty), new QuestionSetWrapper<String, Integer>(movies, years));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
			break;
		case 1:
			LinkedHashMap<String, SQLGenreSet> genres = new LinkedHashMap<>();
			connection = DBConnect.getConnection();
			try {
				skip = maxSkipNumber(choiceSize, difficulty) / 2;
				skip += (int) (Math.random() * skip);
				skip = skip * 3 / 4;
				String qry = null;
				// preQuery selects 'choiceSize' number of rows from movies to
				// join into movie_genre
				String preQuery = "(SELECT * from (SELECT @row := @row +1 as rownum, name as name, mid as mid, rating as rating FROM (SELECT @row := 0) r, movie.movies) ranked WHERE rownum % "
						+ skip + " = 1 AND rating > " + getRating(difficulty) + ")";

				preQuery = "(select M.name as name, M.mid as mid from movie.movies as M where M.mid % " + skip
						+ " = 1 and M.rating > " + getRating(difficulty) + ")";

				qry = "select movie.movie_genre.genre as m_genre, T.name as m_name from movie.movie_genre inner join "
						+ preQuery + "as T on T.mid = movie.movie_genre.mid";

				Statement s = connection.createStatement();
				System.out.println("Executing: " + qry);
				rs = s.executeQuery(qry);
				while (rs.next() && genres.size() <= choiceSize) {
					String nameString = rs.getString("m_name");
					String genreString = rs.getString("m_genre");
					if (!genres.containsKey(nameString)) {
						genres.put(nameString, new SQLGenreSet());
					}
					SQLGenreSet sqlGenre = genres.get(nameString);
					sqlGenre.addGenre(genreString);
				}
				questionCache.put(getCacheKey(1, difficulty), new QuestionSetWrapper<String, SQLGenreSet>(
						new ArrayList<String>(genres.keySet()), new ArrayList<SQLGenreSet>(genres.values())));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
			break;
		case 2:
		case 3:

			skip = 10 + (int) (Math.random() * 180000);
			connection = DBConnect.getConnection();
			LinkedHashMap<String, SQLGenreSet> map3 = new LinkedHashMap<>();
			try {
				String qry = "SELECT a.name as actor, m.name as movie FROM movie.actors a, movies.movie m "
						+ "INNER JOIN movies.acts act ON m.mid = acts.mid and acts.aid = a.aid "
						+ "WHERE m.mid IN (SELECT mid from (SELECT @row := @row +1 AS rownum, mid as mid"
						+ "rating FROM (SELECT @row := 0) r, movie.movies offset " + ") ranked WHERE rownum % " + skip
						+ " = 1 AND rating > " + getRating(difficulty) + ")";
				qry = "select A.name as actor, M.name as movie from movie.actors as A inner join movie.acts as AM on A.aid = AM.aid inner join movie.movies M on AM.mid = M.mid where M.mid % "
						+ skip + " = 1 and M.rating > " + getRating(difficulty);
				Statement s3 = connection.createStatement();
				System.out.println("Executing: " + qry);
				rs = s3.executeQuery(qry);
				while (rs.next() && map3.size() <= choiceSize) {
					String actor = rs.getString("actor");
					String movie = rs.getString("movie");
					if (!map3.containsKey(actor)) {
						map3.put(actor, new SQLGenreSet());
					}
					SQLGenreSet set = map3.get(actor);
					set.addGenre(movie);
				}
				questionCache.put(getCacheKey(3, difficulty), new QuestionSetWrapper<String, SQLGenreSet>(
						new ArrayList<String>(map3.keySet()), new ArrayList<SQLGenreSet>(map3.values())));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
			break;

		case 4:
		case 5:
		case 6:
		case 7:
			if (!cacheContainsKey(9, difficulty)) {
				loadIndex(9, difficulty, choiceSize, skipElements);
			}
			skip = 10 + (int) (Math.random() * 180000);
			connection = DBConnect.getConnection();
			ArrayList<Integer> idList2 = new ArrayList<>(choiceSize * 2);
			try {
				String qry = "(SELECT * from (SELECT @row := @row +1 as rownum, aid as aid, "
						+ "mid as mid FROM (SELECT @row := 0) r, movie.acts) "
						+ "ranked WHERE rownum % 1 = 0 order by mid desc limit " + skip + ", 200000)";
				qry = "select A.aid as aid, A.mid as mid from movie.acts as A limit " + skip + ", 200000";
				Statement s = connection.createStatement();
				System.out.println("Executing: " + qry);
				rs = s.executeQuery(qry);

				// rs.next() is actually very slow so we would
				// like to minimize the time by caching the results
				while (rs.next() && actorNames.size() <= choiceSize * 2) {
					int mid = rs.getInt("mid");
					int aid = rs.getInt("aid");
					idList.add(mid);
					idList2.add(aid);
				}
				questionCache.put(getCacheKey(7, difficulty),
						new QuestionSetWrapper<Integer, Integer>(idList, idList2));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
			break;
		case 8:
			// we'll use 8 as a way to load random actors
			skip = Question.maxSkipNumberActorOverall(choiceSize) / 2;
			skip += (int) (Math.random() * skip);
			skip = skip * 3 / 4;
			connection = DBConnect.getConnection();

			try {
				String qry = "(SELECT * from (SELECT @row := @row +1 as rownum, name as a_name, aid as a_aid FROM (SELECT @row := 0) r, movie.actors) ranked WHERE rownum % "
						+ skip + " = 0)";
				Statement s = connection.createStatement();
				System.out.println("Executing: " + qry);
				rs = s.executeQuery(qry);

				// rs.next() is actually very slow so we would
				// like to minimize the time by caching the results
				while (rs.next() && actorNames.size() <= choiceSize) {
					String name = rs.getString("a_name");
					int aid = rs.getInt("a_aid");
					if (!name.matches("(\\s)*")) {
						actorNames.add(name);
						idList.add(aid);
					}
				}
				questionCache.put(getCacheKey(8, difficulty),
						new QuestionSetWrapper<Integer, String>(idList, actorNames));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
			break;
		case 9:
			// we'll use 9 as a way to load random movies
			skip = maxSkipNumber(choiceSize, difficulty) / 2;
			skip += (int) (Math.random() * skip);
			skip = skip * 3 / 4;
			connection = DBConnect.getConnection();

			try {
				String qry = "(SELECT * from (SELECT @row := @row +1 as rownum, name as m_name, mid as mid FROM (SELECT @row := 0) r, movie.movies) ranked WHERE rownum % "
						+ skip + " = 0)";
				Statement s = connection.createStatement();
				System.out.println("Executing: " + qry);
				rs = s.executeQuery(qry);

				// rs.next() is actually very slow so we would
				// like to minimize the time by caching the results
				while (rs.next() && actorNames.size() <= choiceSize) {
					String name = rs.getString("m_name");
					int mid = rs.getInt("mid");
					if (!name.matches("(\\s)*")) {
						movies.add(name);
						idList.add(mid);
					}
				}
				questionCache.put(getCacheKey(9, difficulty), new QuestionSetWrapper<Integer, String>(idList, movies));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
			break;
		case 10:
			skip = maxSkipNumberActorWithBirthday(choiceSize) / 2;
			skip += (int) (Math.random() * skip);
			skip = skip * 3 / 4;
			connection = DBConnect.getConnection();

			rs = null;
			try {
				String qry = "select * from (select @row := @row +1 as rownum, A.name as a_name, A.Date_of_birth as a_DOB from (SELECT @row := 0) r, movie.actors as A where A.Date_of_birth like '%%%%-%%-%%') ranked where rownum % "
						+ skip + " = 1";
				qry = "select A.name as a_name, A.Date_of_birth as a_DOB from movie.actors as A where A.Date_of_birth like '%%%%-%%-%%' and aid % "
						+ skip + " = 1";
				Statement s = connection.createStatement();
				System.out.println("Executing: " + qry);
				rs = s.executeQuery(qry);

				// rs.next() is actually very slow so we would
				// like to minimize the time by caching the results
				while (rs.next() && actorNames.size() <= choiceSize) {
					String name = rs.getString("a_name");
					String dob = rs.getString("a_DOB");

					if (!dob.matches("(\\s)*") && !name.matches("(\\s)*")) {
						actorNames.add(name);
						actorDOBs.add(dob);
					}
				}
				questionCache.put(getCacheKey(10, difficulty),
						new QuestionSetWrapper<String, String>(actorNames, actorDOBs));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
			break;
		default:
		}
	}

	/**
	 * 711 movies have rating > 4, 14343 movies have rating > 3, 21159 movies
	 * have rating > 2, 22159 movies have rating > 1, 22381 movies have rating >
	 * 0
	 * 
	 * 
	 * 
	 * @param choiceSize
	 * @param difficulty
	 * @param qType
	 * @return
	 */
	private static int maxSkipNumber(int choiceSize, int difficulty) {
		switch (difficulty) {
		case 0:
			return 711 / choiceSize - 1;
		case 1:
			return 14343 / choiceSize - 1;
		case 2:
			return 21159 / choiceSize - 1;
		case 3:
		default:
			return 22381 / choiceSize - 1;
		}
	}

	private static int maxSkipNumberActorWithBirthday(int choiceSize) {
		return 38726 / choiceSize;
	}

	private static int maxSkipNumberActorOverall(int choiceSize) {
		return 184144 / choiceSize;
	}

	private static int getCacheKey(int qType, int difficulty) {
		// questionTypeN * qType + difficulty when fully implemented
		return 20 * qType + difficulty;
	}

	/**
	 * Generates a QuestionSetWrapper containing valid question, answer pairs
	 * Skip elements introduces randomness to the result query but at the cost
	 * of perhaps not having enough elements in the result query if the
	 * ResultSet was small to begin with
	 *
	 * @param difficulty
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private static Question generateQuestion(int difficulty, int choiceSize, boolean skipElements) throws SQLException {

		// Question parameters
		int answer = 0, indexSelected = 0;
		String question = null;
		List<String> choices = new ArrayList<>(choiceLimit);
		ArrayList<SQLGenreSet> genres = null;
		ArrayList<String> movies = null;
		ArrayList<Integer> years = null;
		ArrayList<Integer> aids = null;
		ArrayList<Integer> mids = null;
		boolean dbConnect = false;
		switch ((int) (Math.random() * questionTypeN)) {
		case 0:
			// what year is this movie released?
			if (!questionCache.containsKey(getCacheKey(0, difficulty)) || Math.random() <= refreshThreshold) {
				dbConnect = true;
				loadIndex(0, difficulty, choiceSize, skipElements);
			}

			movies = (ArrayList<String>) questionCache.get(getCacheKey(0, difficulty)).getPropertyList();
			years = (ArrayList<Integer>) questionCache.get(getCacheKey(0, difficulty)).getAnswerList();
			//
			if (movies.size() < resultThreshold) {
				System.out.println("Insufficient amount of elements for question type: (" + movies.size() + ", 0)");
				System.out.println("Re-generating question without skipping");
				return generateQuestion(difficulty, choiceSize, false);
			} else {
				System.out.println("Valid result set, proceeding with computations");
				if (!questionCache.containsKey(getCacheKey(0, difficulty)) || dbConnect) {
					questionCache.put(getCacheKey(0, difficulty),
							new QuestionSetWrapper<String, Integer>(movies, years));
				}
			}
			indexSelected = (int) (Math.random() * movies.size());
			question = "What year was " + movies.get(indexSelected) + " first released?";
			int yearSelected = years.get(indexSelected);
			String yearStr = Integer.toString(yearSelected);
			// populate choices with (choiceLimit - 1) elements
			for (int i = 0; i < choiceLimit - 1; ++i) {

				if (Math.random() > 0.70) {
					int randomCloseYear = yearSelected;
					if (yearSelected < 2000 && Math.random() > 0.50) {
						randomCloseYear += 1 + ((int) (10 * Math.random()));
					} else {
						randomCloseYear -= 1 + ((int) (10 * Math.random()));
					}
					String randomCloseYearString = Integer.toString(randomCloseYear);
					if (!choices.contains(randomCloseYearString)) {
						choices.add(randomCloseYearString);
					} else {
						--i;
					}
				} else {
					// random number from 1900 to 2015
					int randomYear = 1900 + ((int) (Math.random() * 116));
					String randomYearString = Integer.toString(randomYear);
					if (randomYear != yearSelected && !choices.contains(randomYearString)) {
						choices.add(randomYearString);
					} else {
						// just redo calculation
						--i;
					}
				}
			}
			answer = (int) (Math.random() * (choices.size() + 1));
			choices.add(answer, yearStr);
			return new Question(question, choices, answer);
		case 1:
			// what set of genres best matches this movie?
			if (!questionCache.containsKey(getCacheKey(1, difficulty)) || Math.random() <= refreshThreshold) {
				dbConnect = true;
				loadIndex(1, difficulty, choiceSize, skipElements);
			}
			movies = (ArrayList<String>) questionCache.get(getCacheKey(1, difficulty)).getPropertyList();
			genres = (ArrayList<SQLGenreSet>) questionCache.get(getCacheKey(1, difficulty)).getAnswerList();

			if (movies.size() < resultThreshold) {
				System.out.println("Insufficient amount of elements for question type: (" + movies.size() + ", 1)");
				System.out.println("Re-generating question without skipping");
				return generateQuestion(difficulty, choiceSize, false);
			} else {
				System.out.println("Valid result set, proceeding with computations");
			}
			indexSelected = (int) (Math.random() * movies.size());
			String movieSelected = movies.get(indexSelected);
			question = "What set of genres best matches the movie " + movieSelected + "?";
			SQLGenreSet genreSetSelected = genres.get(indexSelected);
			HashSet<SQLGenreSet> genresChosen = new HashSet<>();
			for (int i = 0; i < choiceLimit - 1; ++i) {
				int index = (int) (Math.random() * movies.size());
				SQLGenreSet genreSet = genres.get(index);
				if (index != indexSelected && !genresChosen.contains(genreSet) && !genreSet.equals(genreSetSelected)) {
					choices.add(genreSet.toString());
					genresChosen.add(genreSet);
				} else if (Math.random() > 0.10) {
					--i;
				}
			}
			answer = (int) (Math.random() * (choices.size() + 1));
			choices.add(answer, genreSetSelected.toString());
			return new Question(question, choices, answer);
		case 2:
			// which of these movies is the oldest?
			movies = null;
			years = null;
			if (!questionCache.containsKey(getCacheKey(0, difficulty)) || Math.random() <= refreshThreshold) {
				dbConnect = true;
				loadIndex(0, difficulty, choiceSize, skipElements);
			}

			movies = (ArrayList<String>) questionCache.get(getCacheKey(0, difficulty)).getPropertyList();
			years = (ArrayList<Integer>) questionCache.get(getCacheKey(0, difficulty)).getAnswerList();

			if (movies.size() < resultThreshold) {
				System.out.println("Insufficient amount of elements for question type: (" + movies.size() + ", 3)");
				System.out.println("Re-generating question without skipping");
				return generateQuestion(difficulty, choiceSize, false);
			} else {
				System.out.println("Valid result set, proceeding with computations");
			}
			// randomly generate 5 movies that were made in different years
			TreeMap<Integer, String> movieYearPair = new TreeMap<>();
			for (int i = 0; i < choiceLimit; ++i) {
				indexSelected = (int) (Math.random() * movies.size());
				if (movieYearPair.containsKey(years.get(indexSelected))) {
					--i;
					continue;
				}
				movieYearPair.put(years.get(indexSelected), movies.get(indexSelected));
			}
			movies = new ArrayList<String>(movieYearPair.values());
			years = new ArrayList<Integer>(movieYearPair.keySet());
			years.remove(0);
			String lowestYearString = movies.remove(0);
			int iter_size = movies.size();
			for (int i = 0; i < iter_size; ++i) {
				int random_index = (int) (movies.size() * Math.random());
				choices.add(movies.remove(random_index));
				years.remove(random_index);
			}
			answer = (int) (Math.random() * (choices.size() + 1));
			choices.add(answer, lowestYearString);
			question = "Which of these movies (";
			for (int i = 0; i < choices.size(); ++i) {
				question = question + choices.get(i) + ((i == choices.size() - 1) ? "" : ", ");
			}
			question = question + ") was made the earliest?";
			return new Question(question, choices, answer);
		case 3:
 /*
			// which movie has actor X acted in
			if (!cacheContainsKey(3, difficulty) || Math.random() <= refreshThreshold) {
				loadIndex(3, difficulty, choiceSize, skipElements);
			}
			// Instead of movies and genres, they are actors and movies but
			// that's good enough
			movies = (ArrayList<String>) questionCache.get(getCacheKey(3, difficulty)).getPropertyList();
			genres = (ArrayList<SQLGenreSet>) questionCache.get(getCacheKey(3, difficulty)).getAnswerList();

			if (movies.size() < resultThreshold) {
				System.out.println("Insufficient amount of elements for question type: (" + movies.size() + ", 1)");
				System.out.println("Re-generating question without skipping");
				return generateQuestion(difficulty, choiceSize, false);
			} else {
				System.out.println("Valid result set, proceeding with computations");
			}
			indexSelected = (int) (Math.random() * movies.size());
			movieSelected = movies.get(indexSelected);
			question = "What movie has " + movieSelected + " acted in?";
			genreSetSelected = genres.get(indexSelected);
			HashSet<String> chosen3 = new HashSet<>();
			String answer3 = genreSetSelected.getList().remove(0);
			genreSetSelected.addGenre(answer3);
			chosen3.add(answer3);

			for (int i = 0; i < choiceLimit - 1; ++i) {
				int index = (int) (Math.random() * movies.size());
				SQLGenreSet genreSet = genres.get(index);
				if (index != indexSelected) {
					if (genreSet.getList().size() == 0) {
						--i;
						continue;
					}
					String movanswer = genreSet.getList().remove(0);
					if (!genreSetSelected.contains(movanswer) && !chosen3.contains(movanswer)) {
						choices.add(movanswer);
						chosen3.add(movanswer);
					} else {
						--i;
					}
				} else if (Math.random() > 0.10) {
					--i;
				}
			}
			answer = (int) (Math.random() * (choices.size() + 1));
			choices.add(answer, answer3);
			return new Question(question, choices, answer);*/

		case 4:

			/*// which movie has actor X not acted in
			if (!cacheContainsKey(3, difficulty) || Math.random() <= refreshThreshold) {
				loadIndex(3, difficulty, choiceSize, skipElements);
			}
			// Instead of movies and genres, they are actors and movies but
			// that's good enough
			movies = (ArrayList<String>) questionCache.get(getCacheKey(3, difficulty)).getPropertyList();
			genres = (ArrayList<SQLGenreSet>) questionCache.get(getCacheKey(3, difficulty)).getAnswerList();

			if (movies.size() < resultThreshold) {
				System.out.println("Insufficient amount of elements for question type: (" + movies.size() + ", 1)");
				System.out.println("Re-generating question without skipping");
				return generateQuestion(difficulty, choiceSize, false);
			} else {
				System.out.println("Valid result set, proceeding with computations");
			}
			indexSelected = (int) (Math.random() * movies.size());
			movieSelected = movies.get(indexSelected);
			question = "What movie has " + movieSelected + " NOT acted in?";
			genreSetSelected = genres.get(indexSelected);
			HashSet<String> chosen4 = new HashSet<>();
			for (int i = 0; i < choiceLimit - 1 && i < genreSetSelected.getList().size(); ++i) {
				chosen4.add(genreSetSelected.getList().get(i));
			}
			String answer4 = null;
			while (answer4 == null) {
				int index = (int) (Math.random() * movies.size());
				SQLGenreSet genreSet = genres.get(index);
				if (index != indexSelected) {
					if (genreSet.getList().size() == 0) {
						continue;
					}
					String movanswer = genreSet.getList().remove(0);
					if (!genreSetSelected.contains(movanswer) && !chosen4.contains(movanswer)) {
						choices.add(movanswer);
						answer4 = movanswer;
					}
				}
			}
			answer = (int) (Math.random() * (choices.size() + 1));
			choices.add(answer, answer4);
			return new Question(question, choices, answer);
		*/
		case 5:
		case 6:
			// just re-generate question since incomplete code
			return generateQuestion(difficulty, choiceSize, skipElements);
		case 7:
			// has actor X acted in same movie as actor Y?
			if (!cacheContainsKey(7, difficulty) || Math.random() <= refreshThreshold) {
				loadIndex(7, difficulty, choiceSize, skipElements);
			}
			mids = (ArrayList<Integer>) questionCache.get(getCacheKey(7, difficulty)).getPropertyList();
			aids = (ArrayList<Integer>) questionCache.get(getCacheKey(7, difficulty)).getAnswerList();
			int actor1 = -1, actor2 = -1;
			for (int i = 0; i < choiceSize * 2 && (actor1 == -1 || actor2 == -1); i++) {
				int randomVal = aids.get((int) (Math.random() * mids.size()));
				if (actor1 == -1) {
					actor1 = randomVal;
				} else if (randomVal == actor1) {
					continue;
				} else {
					actor2 = randomVal;
					break;
				}
			}
			Connection conn = DBConnect.getConnection();
			HashSet<Integer> actor1MovieSet = new HashSet<>();
			HashSet<Integer> actor2MovieSet = new HashSet<>();
			String a1Name = "", a2Name = "";
			try {
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery("select mid as mid from movie.acts where aid = " + actor1);
				while (rs.next()) {
					int movieID = rs.getInt("mid");
					actor1MovieSet.add(movieID);
				}
				statement = conn.createStatement();
				rs = statement.executeQuery("select mid as mid from movie.acts where aid = " + actor1);
				while (rs.next()) {
					int movieID = rs.getInt("mid");
					actor2MovieSet.add(movieID);
				}
				statement = conn.createStatement();
				rs = statement.executeQuery("select name as name from movie.actors where aid = " + actor1);
				if (rs.next()) {
					a1Name = rs.getString("name");
				} else {
					System.out.println("actor id " + actor1 + " does not have a name");
				}
				statement = conn.createStatement();
				rs = statement.executeQuery("select name as name from movie.actors where aid = " + actor2);
				if (rs.next()) {
					a2Name = rs.getString("name");
				} else {
					System.out.println("actor id " + actor2 + " does not have a name");
				}
			} finally {
				if (conn != null) {
					conn.close();
				}
			}
			answer = 1;
			for (int mid : actor1MovieSet) {
				if (actor2MovieSet.contains(mid)) {
					answer = 0;
					break;
				}
			}
			question = "Have " + a1Name + " and " + a2Name + " acted in the same movie?";
			choices.add("True");
			choices.add("False");
			return new Question(question, choices, answer);
		case 8:
			// which set of genres is movie X NOT about?
			if (!questionCache.containsKey(getCacheKey(1, difficulty)) || Math.random() <= refreshThreshold) {
				dbConnect = true;
				loadIndex(1, difficulty, choiceSize, skipElements);
			}
			movies = (ArrayList<String>) questionCache.get(getCacheKey(1, difficulty)).getPropertyList();
			genres = (ArrayList<SQLGenreSet>) questionCache.get(getCacheKey(1, difficulty)).getAnswerList();
			if (movies.size() < resultThreshold) {
				System.out.println("Insufficient amount of elements for question type: (" + movies.size() + ", 8)");
				System.out.println("Re-generating question without skipping");
				return generateQuestion(difficulty, choiceSize, false);
			} else {
				System.out.println("Valid result set, proceeding with computations");
			}
			indexSelected = (int) (Math.random() * movies.size());
			movieSelected = movies.get(indexSelected);
			question = "Which of these genres does not match the genre of " + movieSelected + "?";
			genreSetSelected = genres.get(indexSelected);
			if (genreSetSelected.size() == 0) {
				return generateQuestion(difficulty, choiceSize, skipElements);
				// just redo, we selected bad data on random
			}
			ArrayList<String> choiceCandidates = genreSetSelected.getScrambledList();
			for (int i = 0; i < choiceLimit - 1 && i < choiceCandidates.size(); ++i) {
				choices.add(choiceCandidates.get(i));
			}
			answer = (int) (Math.random() * (choices.size() + 1));
			ArrayList<String> genresSetComplement = new ArrayList<>();
			for (String s : ALL_GENRES) {
				if (!genreSetSelected.contains(s)) {
					genresSetComplement.add(s);
				}
			}
			if (genresSetComplement.isEmpty()) {
				System.out.println("Genres set complement is empty, re-generating question.");
				System.out.println("Genres set was " + genreSetSelected.toString());
				return generateQuestion(difficulty, choiceSize, skipElements);
			}
			choices.add(answer, genresSetComplement.get((int) (Math.random() * genresSetComplement.size())));
			return new Question(question, choices, answer);
		case 9:

		case 10:
			// which actor is associated with date of birth x?
			ArrayList<String> aName = null;
			ArrayList<String> aDOB = null;
			if (!questionCache.containsKey(getCacheKey(10, difficulty)) || Math.random() <= refreshThreshold) {
				dbConnect = true;
				loadIndex(10, difficulty, choiceSize, skipElements);
			}
			aName = (ArrayList<String>) questionCache.get(getCacheKey(10, difficulty)).getPropertyList();
			aDOB = (ArrayList<String>) questionCache.get(getCacheKey(10, difficulty)).getAnswerList();
			if (aName.size() < resultThreshold) {
				System.out.println("Insufficient amount of elements for question type: (" + aName.size() + ", 10)");
				System.out.println("Re-generating question without skipping");
				return generateQuestion(difficulty, choiceSize, false);
			} else {
				System.out.println("Valid result set, proceeding with computations");
			}
			ArrayList<Integer> randomSelects = new ArrayList<>();
			for (int i = 0; i < choiceLimit; ++i) {
				int randomIndex = (int) (Math.random() * aName.size());
				if (!randomSelects.contains(randomIndex) && !choices.contains(aDOB.get(randomIndex))) {
					randomSelects.add(randomIndex);
					choices.add(aDOB.get(randomIndex));
				} else {
					--i;
				}
			}
			// randomSelects now contains 5 random indices to (actor, DOB) pair
			answer = (int) (Math.random() * randomSelects.size());
			question = "On which day was actor " + aName.get(randomSelects.get(answer)) + " born? (YYYY-MM-DD)";
			return new Question(question, choices, answer);
		default:
		}
		return null;
	}

	/**
	 *
	 * @param
	 * 			<P>
	 *            - Property
	 * @param <A>
	 *            - Answer associated with property
	 */
	private static class QuestionSetWrapper<P, A> {

		private List<P> pList = null;
		private List<A> aList = null;

		private QuestionSetWrapper(List<P> pList, List<A> aList) {
			this.pList = pList;
			this.aList = aList;
		}

		public List<P> getPropertyList() {
			return pList;
		}

		public List<A> getAnswerList() {
			return aList;
		}

		@SuppressWarnings("unused")
		public int getSize() {
			return pList.size();
		}
	}
}
