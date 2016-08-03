package org.davidcampelo.post.model;

import java.util.ArrayList;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class Question {

    long id;
    String title;
    QuestionType type;
    ArrayList<Option> allOptions;
    ArrayList<Option> markedOptions;

    enum QuestionType {
        MULTIPLE_CHOICE,
        SINGLE_CHOICE
    }

    public Question() {

    }

    public Question(long id, String title, QuestionType type, ArrayList<Option> allOptions, ArrayList<Option> markedOptions) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.allOptions = markedOptions;
        this.markedOptions = markedOptions;
    }


    public String getTitle() {
        return title;
    }

    public QuestionType getType() {
        return type;
    }

    public ArrayList<Option> getAllOptions() {
        return allOptions;
    }

    public ArrayList<Option> getMarkedOptions() {
        return markedOptions;
    }
}
