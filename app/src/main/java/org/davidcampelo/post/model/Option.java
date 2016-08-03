package org.davidcampelo.post.model;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class Option {
    long   id;
    String text;


    public Option() {

    }

    public Option(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Option(String text) {
        this(0, text);
    }



    public String getText() {
        return text;
    }
}
