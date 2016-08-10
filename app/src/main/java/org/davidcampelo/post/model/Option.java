package org.davidcampelo.post.model;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class Option {
    long id;
    String text;
    Question question;
    PublicOpenSpace publicOpenSpace;

    public Option() {
    }

    public Option(long id, String text, Question question, PublicOpenSpace publicOpenSpace) {
        this.id = id;
        this.text = text;
        this.question = question;
        this.publicOpenSpace = publicOpenSpace;
    }

    public Option(String text, Question question) {
        this(0, text, question, null);
    }

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

    public void setPublicOpenSpace(PublicOpenSpace publicOpenSpace) {
        this.publicOpenSpace = publicOpenSpace;
    }
}
