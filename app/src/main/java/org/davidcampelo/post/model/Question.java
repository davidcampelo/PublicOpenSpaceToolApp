package org.davidcampelo.post.model;

import org.w3c.dom.ProcessingInstruction;

import java.util.ArrayList;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class Question {

    long id;
    String number;
    String title;
    QuestionType type;
    private ArrayList<Option> options;


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

    public Question(long id, String number, String title, QuestionType type, ArrayList<Option> options) {
        this.id = id;
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

    public void setOptions(ArrayList<Option> options) {
        synchronized (options) {
            this.options = options;
        }
    }

}
