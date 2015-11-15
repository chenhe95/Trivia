Trivia
Motivation: We thought it was a fun way to incorporate a movie database to make a trivia/quiz game to
test users’ movie knowledge.
Features that will definitely be implemented:
-General functionality of having to answer questions – based on the information given
  - Some question examples include:
    -Which actor A acted in movie M? 
    -What genre G does director D mostly direct?
    -What was the highest rated movie M that actor A participated in?
    -What was the release year of Movie M?
  - Most likely multiple choice with just clicking the button as the answer
- Has a timer and a score
- User login/database of users
- Difficulty levels – choose difficulty levels that is determined by the ratings of movies...easier difficulty means higher rated movies (more well known) will show up while harder means lower rated movies will show up (more obscure). Scores will be based on that 

Features that may be implemented (Extra Credit options):
-Connect with social media (Use FB API)
- Trigger Bing search on wrong answer
- NoSQL component – store user login info, game history, and highest scores

Technologies:
- Oracle for DB
- MongoDB for NoSql component (Extra Credit Option)
- Java for cleaning DB data and frontend

Responsibilities: (TODO)
- Jason Tang – clean up the data, come up with schema design
- He Chen – design front end using Java Swing, establishing DB connection
- Fanglin Lu – think of ideas for NoSQL part, set up AWS
- Jay Jung – help with query designs and backend components

Relational Schema:
Movies (mid integer, name VarChar(30), rating double, director VarChar(30))- key mid
Movies_genres (mid integer, genre VarChar(15))- key mid, genre
Acts (mid integer, aid integer)- key mid, aid
Actors (aid integer, name VarChar(30), Date_of_birth Date, Date_of_death Date)- key aid
