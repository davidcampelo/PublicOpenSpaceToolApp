package org.davidcampelo.post.model;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class Option {
    long id;
    String text;
    Question question;

    public Option() {
    }

    public Option(long id, String text, Question question) {
        this.id = id;
        this.text = text;
        this.question = question;
    }

    public Option(String text, Question question) {
        this(0, text, question);
    }


    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }
}
