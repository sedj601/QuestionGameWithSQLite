/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package questongamewithsqlite;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 *
 * @author blj0011
 */
public class FXMLDocumentController implements Initializable
{

    DatabaseHandler databaseHandler;
    List<Question> questions;
    List<Button> buttonContainer;
    int currentQuestionCounter;

    @FXML
    private TextArea taDisplay;
    @FXML
    private Button btnA, btnB, btnC, btnD, btnAddNewQuestionToDb;

    @FXML
    private void handleButtonCheckForCorrectAnswer(ActionEvent event)
    {
        btnAddNewQuestionToDb.setDisable(true);

        Button tempBtn = (Button) event.getSource();

        //Check to see if the button press answer equals the answer
        if (tempBtn.getUserData().toString().equals(questions.get(currentQuestionCounter).getAnswer())) {
            rightAnswerAlert();
        }
        else {
            wrongAnswerAlert(questions.get(currentQuestionCounter).getAnswer());
        }
    }

    @FXML
    private void handleMiFileClose(ActionEvent event)
    {
        databaseHandler.closeConnection();
        System.out.println("Closing database connection!");
        Platform.exit();
    }

    @FXML
    private void handleAddNewQuestionToDb(ActionEvent event)
    {
        // Create the custom dialog.
        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle("Add Question To DB Dialog");
        dialog.setHeaderText("Enter Question Information");

// Set the icon (must be included in the project).
        Label img = new Label();
        img.getStyleClass().addAll("alert", "error", "dialog-pane");
        dialog.setGraphic(img);

// Set the button types.
        ButtonType addButtonType = new ButtonType("Add", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField question = new TextField();
        question.setPromptText("Enter question");
        TextField answer = new TextField("");
        answer.setPromptText("Enter answer");
        TextField incorrectAnswer1 = new TextField();
        incorrectAnswer1.setPromptText("Enter incorrect Answer");
        TextField incorrectAnswer2 = new TextField("");
        incorrectAnswer2.setPromptText("Enter incorrect Answer");
        TextField incorrectAnswer3 = new TextField();
        incorrectAnswer3.setPromptText("Enter incorrect Answer");

        grid.add(new Label("Question:"), 0, 0);
        grid.add(question, 1, 0);
        grid.add(new Label("Answer:"), 0, 1);
        grid.add(answer, 1, 1);
        grid.add(new Label("Incorrect Answer 1:"), 0, 2);
        grid.add(incorrectAnswer1, 1, 2);
        grid.add(new Label("Incorrect Answer 2:"), 0, 3);
        grid.add(incorrectAnswer2, 1, 3);
        grid.add(new Label("Incorrect Answer 3:"), 0, 4);
        grid.add(incorrectAnswer3, 1, 4);

// Enable/Disable login button depending on whether a username was entered.
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        BooleanBinding booleanBind = question.textProperty().isEmpty()
                .or(answer.textProperty().isEmpty())
                .or(incorrectAnswer1.textProperty().isEmpty())
                .or(incorrectAnswer2.textProperty().isEmpty())
                .or(incorrectAnswer3.textProperty().isEmpty());
        addButton.disableProperty().bind(booleanBind);

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> question.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                List<String> tempIncorrectAnswer = new ArrayList();
                tempIncorrectAnswer.add(incorrectAnswer1.getText());
                tempIncorrectAnswer.add(incorrectAnswer2.getText());
                tempIncorrectAnswer.add(incorrectAnswer3.getText());

                Question tempQuestion = new Question(-1, question.getText(), answer.getText(), tempIncorrectAnswer);

                if (databaseHandler.addNewQuestionToDatabase(tempQuestion)) {
                    questions.add(tempQuestion);
                    Collections.shuffle(questions);
                    currentQuestionCounter = 0;
                    setQuestion(currentQuestionCounter);
                }
                else {
                    System.out.println("The database cannot have two identical questions!");
                }

                return tempQuestion;
            }
            return null;
        });

        dialog.showAndWait();

//        result.ifPresent(item -> {
//            System.out.println("Question: " + item.getQuestion() + "\n"
//                    + "Answer: " + item.getAnswer() + "\n"
//                    + "Incorrect Answers: \n\t"
//                    + String.join("\n\t", item.getIncorrectAnswers()));
//        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

        buttonContainer = new ArrayList();//Used to hold GUI buttons.
        buttonContainer.add(btnA);
        buttonContainer.add(btnB);
        buttonContainer.add(btnC);
        buttonContainer.add(btnD);

        //Get all questions
        currentQuestionCounter = 0;
        databaseHandler = new DatabaseHandler();
        questions = databaseHandler.getAllQuestionsFromDB();

        //Set first question.
        setQuestion(currentQuestionCounter);
    }

    public void setQuestion(int currentQuestion)
    {
        if (currentQuestion < questions.size()) {
            taDisplay.setText(questions.get(currentQuestion).getQuestion() + "\n\n");//Display the question.

            List<String> allAnswers = new ArrayList<>(questions.get(currentQuestion).getIncorrectAnswers());//Add the incorrect answers.
            allAnswers.add(questions.get(currentQuestion).getAnswer());//Add the correct answer.
            Collections.shuffle(allAnswers);//Randomize all answers.
            for (int i = 0; i < buttonContainer.size(); i++) {
                buttonContainer.get(i).setUserData(allAnswers.get(i));//Set the buttons' userdata to an answer
                taDisplay.appendText("\t" + buttonContainer.get(i).getText() + ": " + allAnswers.get(i) + System.lineSeparator());
            }
        }
        else {
            taDisplay.setText("Thanks for playing!");
            buttonContainer.forEach((item) -> {
                item.setDisable(true);
            });
        }
    }

    public void rightAnswerAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Correct Answer Alert");
        alert.setHeaderText("Correct Answer:");
        alert.setContentText("Correct answer. Great Job!");
        alert.showAndWait();

        setQuestion(++currentQuestionCounter);
    }

    public void wrongAnswerAlert(String rightAnswer)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Incorrect Answer Alert");
        alert.setHeaderText("Incorrect Answer:");
        alert.setContentText("Incorrect answer! The correct answer is: " + rightAnswer);
        alert.showAndWait();

        setQuestion(++currentQuestionCounter);
    }
}
