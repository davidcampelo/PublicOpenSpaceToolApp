package org.davidcampelo.post.model;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class Option {
    long id;
    String text;
    boolean checked;

    public Option() {
    }

    public Option(long id, String text, boolean checked) {
        this.id = id;
        this.text = text;
        this.checked = checked;
    }

    public Option(String text, boolean checked) {
        this(0, text, checked);
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
