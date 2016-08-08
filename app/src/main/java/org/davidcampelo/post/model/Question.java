package org.davidcampelo.post.model;

import org.w3c.dom.ProcessingInstruction;

import java.util.ArrayList;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class Question {

    String number;
    String title;
    QuestionType type;
    ArrayList<Option> options;
    /**
     * Answer text depends on QuestionType:
     * - SINGLE_CHOICE: opt_id
     * - MULTIPLE_CHOICE: opt_id's separated by a character separator (slash == \)
     * - INPUT_TEXT/NUMBER/DECIMAL: Text typed by the user
     */
    String answers;

    public enum QuestionType {
        MULTIPLE_CHOICE,
        SINGLE_CHOICE,
        INPUT_NUMBER,
        INPUT_DECIMAL,
        INPUT_TEXT,
        MATRIX_VALUES
    }

    public Question() {

    }

    public Question(String number, String title, QuestionType type, ArrayList<Option> options, String answers) {
        this.number = number;
        this.title = title;
        this.type = type;
        this.options = options;
        this.answers = answers;
    }


    public String getTitle() {
        return title;
    }

    public String getNumber() {
        return number;
    }

    public QuestionType getType() {
        return type;
    }

    public ArrayList<Option> getAllOptions() {
        return options;
    }

    public void removeOtherOptionByText(String otherOptionTitle) {
        ArrayList<Option> toRemove = new ArrayList<Option>();
        for (Option option : options) {
            // option.id == 0 is that it's a New Other Option
            if (option.id == 0 && option.text.indexOf(otherOptionTitle) >= 0)
                toRemove.add(option);
        }
        synchronized (options){
            for (Option option : toRemove){
                options.remove(option);
            }
        }
    }

    public void addOption(Option option) {
        synchronized (options){
            options.add(option);
        }
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

}
