package org.davidcampelo.post.model;

import java.util.ArrayList;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class Question {

    String title;
    ArrayList<Option> options;

    public Question (String title, ArrayList<Option> options){
        this.title = title;
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }
}
