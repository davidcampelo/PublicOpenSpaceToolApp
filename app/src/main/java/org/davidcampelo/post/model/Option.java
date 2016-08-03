package org.davidcampelo.post.model;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class Option {
    long id;
    String text;
    boolean checked;
    Question question;

    public Option() {
    }

    public Option(long id, String text, boolean checked, Question question) {
        this.id = id;
        this.text = text;
        this.checked = checked;
        this.question = question;
    }

    public Option(String text, boolean checked, Question question) {
        this(0, text, checked, question);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }
}
