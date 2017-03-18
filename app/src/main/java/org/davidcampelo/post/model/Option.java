package org.davidcampelo.post.model;

import org.davidcampelo.post.utils.Constants;

import java.util.ArrayList;

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
    /**
     * List of Questions numbers should be disabled in the UI if this Option is checked
     */
    ArrayList<String> disabledQuestionNumbers;

    public Option() {
    }

    public Option(long id, String alias, String value, String title, Question question, ArrayList<String> disabledQuestionNumbers, PublicOpenSpace publicOpenSpace) {
        this.id = id;
        this.alias = alias;
        this.value = value;
        this.title = title;
        this.question = question;
        this.disabledQuestionNumbers = disabledQuestionNumbers;
        this.publicOpenSpace = publicOpenSpace;
    }

    public Option(String alias, String value, String title, Question question, ArrayList<String> disabledQuestionNumbers) {
        this(0, alias, value, title, question, disabledQuestionNumbers, null);
    }

    public String getAlias() {
        return alias;
    }

    public ArrayList<String> getDisabledQuestionNumbers() {
        return disabledQuestionNumbers;
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

    /**
     * Return if it's an "OTHER option" (an Option which enabled users to create new custom Options)
     */
    public boolean isOtherOption() {
        return (alias.indexOf(Constants.MULTIPLE_QUESTION_OTHER_START_STRING) > 0);
    }


    /**
     * Return if it's an "OTHER option" (an Option which indicates the user may create custom Options)
     */
    public boolean wasAddedByUser() {
        return alias.equals(Constants.OPTION_ALIAS_ADDED_BY_USER);
    }
}
