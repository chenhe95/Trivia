
import java.util.List;

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
    
    public Question(String question, List<String> choices, int answer) {
        this.question = question;
        this.choices = choices;
        this.answer = answer;
    }
    
    public List<String> getChoices() {
        return choices;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public int getAnswer() {
        return answer;
    }
}
