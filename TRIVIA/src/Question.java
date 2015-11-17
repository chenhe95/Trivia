
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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

	private static final int questionTypeN = 2;
	private static final int choiceLimit = 5; // maximum number of choices
	// the resulting query must have at least this many valid tuples
	private static final int resultThreshold = 20;
	// 3% chance to generate fresh data on question generation
	private static double refreshThreshold = 0.03;

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
				questionList.add(generateQuestion(difficulty, 4 * questionSetSize, true));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return questionList;
	}

	private static void loadIndex(int qType, int difficulty, int choiceSize) throws SQLException {
		switch (qType) {
		case 0:
			int skip = maxSkipNumber(choiceSize, difficulty) / 2;
			skip += (int) (Math.random() * skip);
			skip = skip * 3 / 4;
			ArrayList<String> movies = new ArrayList<>();
			ArrayList<Integer> years = new ArrayList<>();
			Connection connection = DBConnect.getConnection();
			ResultSet rs = null;
			try {
				String qry = "SELECT * from (SELECT @row := @row +1 AS rownum, name, rating FROM (SELECT @row := 0) r, movie.movies) ranked WHERE rownum % "
						+ skip + " = 1 AND rating > " + getRating(difficulty);

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
					if (year > 0 && !name.equals("")) {
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
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
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

	private static int getCacheKey(int qType, int difficulty) {
                // questionTypeN * qType + difficulty when fully implemented
		return 10 * qType + difficulty;
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
		boolean dbConnect = false;
		switch ((int) (Math.random() * questionTypeN)) {
		case 0:
			ArrayList<String> movies = null;
			ArrayList<Integer> years = null;
			if (!questionCache.containsKey(getCacheKey(0, difficulty)) || Math.random() <= refreshThreshold) {
				dbConnect = true;
				loadIndex(0, difficulty, choiceSize);
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

				// previous algorithm looked up 5 random movie years
				// but it ended up returning similar years
				// int index = (int) (Math.random() * movies.size());
				// if (index != indexSelected) {
				// choices.add(Integer.toString(years.get(index)));
				// } else if (Math.random() > 0.15) {
				// --i;
				// }
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
			movies = null;
			ArrayList<SQLGenreSet> genres = null;
			if (!questionCache.containsKey(getCacheKey(1, difficulty)) || Math.random() <= refreshThreshold) {
				dbConnect = true;
				loadIndex(1, difficulty, choiceSize);
			}
			movies = (ArrayList<String>) questionCache.get(getCacheKey(1, difficulty)).getPropertyList();
			genres = (ArrayList<SQLGenreSet>) questionCache.get(getCacheKey(1, difficulty)).getAnswerList();

			if (movies.size() < resultThreshold) {
				System.out.println("Insufficient amount of elements for question type: (" + movies.size() + ", 1)");
				System.out.println("Re-generating question without skipping");
				return generateQuestion(difficulty, choiceSize, false);
			} else {
				System.out.println("Valid result set, proceeding with computations");
				if (!questionCache.containsKey(getCacheKey(1, difficulty)) || dbConnect) {
					questionCache.put(getCacheKey(1, difficulty),
							new QuestionSetWrapper<String, SQLGenreSet>(movies, genres));
				}
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
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			// which actor is associated with date of birth x?
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
