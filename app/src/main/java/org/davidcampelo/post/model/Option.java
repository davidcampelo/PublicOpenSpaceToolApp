package org.davidcampelo.post.model;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class Option {
    long id;
    String alias;
    String value;
    String title;
    Question question;
    PublicOpenSpace publicOpenSpace;

    public Option() {
    }

    public Option(long id, String alias, String value, String title, Question question, PublicOpenSpace publicOpenSpace) {
        this.id = id;
        this.alias = alias;
        this.value = value;
        this.title = title;
        this.question = question;
        this.publicOpenSpace = publicOpenSpace;
    }

    public Option(String alias, String value, String title, Question question) {
        this(0, alias, value, title, question, null);
    }

    public String getAlias() {
        return alias;
    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public void setPublicOpenSpace(PublicOpenSpace publicOpenSpace) {
        this.publicOpenSpace = publicOpenSpace;
    }
}
