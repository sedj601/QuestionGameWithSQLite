/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package questongamewithsqlite;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author blj0011
 */
public class Question
{

    private int id;
    private String question;
    private String answer;
    private List<String> incorrectAnswers = new ArrayList();

    public Question(int id, String question, String answer, List<String> incorrectAnswers)
    {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.incorrectAnswers = incorrectAnswers;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public List<String> getIncorrectAnswers()
    {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers)
    {
        this.incorrectAnswers = incorrectAnswers;
    }
}
