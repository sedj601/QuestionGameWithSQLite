/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package questongamewithsqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author blj0011
 */
public class DatabaseHandler
{

    private Connection conn;

    public DatabaseHandler()
    {
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            System.out.println("Connection to SQLite has been established.");
        }
        catch (SQLException ex) {
            System.out.println("Connection to SQLite failed!");
            System.out.println(ex.toString());
        }
    }

    public List<Question> getAllQuestionsFromDB()
    {
        List<Question> questions = new ArrayList();

        String sqlQuery = "SELECT * FROM questions;";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            // loop through the result set
            while (rs.next()) {
                List<String> incorrectAnswers = new ArrayList();
                incorrectAnswers.add(rs.getString("incorrect_answer_1"));
                incorrectAnswers.add(rs.getString("incorrect_answer_2"));
                incorrectAnswers.add(rs.getString("incorrect_answer_3"));
                Question question = new Question(rs.getInt("id"), rs.getString("question"), rs.getString("answer"), incorrectAnswers);
                questions.add(question);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return questions;
    }

    public boolean addNewQuestionToDatabase(Question question)
    {
        String sqlQuery = "INSERT INTO questions(question, answer, incorrect_answer_1, incorrect_answer_2, incorrect_answer_3) VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
            pstmt.setString(1, question.getQuestion());
            pstmt.setString(2, question.getAnswer());
            pstmt.setString(3, question.getIncorrectAnswers().get(0));
            pstmt.setString(4, question.getIncorrectAnswers().get(1));
            pstmt.setString(5, question.getIncorrectAnswers().get(2));
            pstmt.executeUpdate();

            return true;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());

            return false;
        }
    }

    public void closeConnection()
    {
        try {
            conn.close();
        }
        catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

}
