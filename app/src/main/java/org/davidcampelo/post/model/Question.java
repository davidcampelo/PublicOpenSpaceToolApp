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

    public enum QuestionType {
        MULTIPLE_CHOICE,    // one or more option may be chosen
        SINGLE_CHOICE,      // one only option may be chosen
        INPUT_NUMBER,       // the answer is a numeric input (integer) set by user
        INPUT_DECIMAL,      // the answer is a numeric input (decimal - with comma) set by user
        INPUT_TEXT,         // the answer is a text input (alphanumeric) set by user
        VARIABLE_SINGLE_CHOICE, // the question may be repeated according to the park answers
                                // (see question 29)
        MATRIX_VALUES       // amount of question answers is variable, used to handle question 29
    }

    public Question() {

    }

    public Question(String number, String title, QuestionType type, ArrayList<Option> options) {
        this.number = number;
        this.title = title;
        this.type = type;
        this.options = options;
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
}
