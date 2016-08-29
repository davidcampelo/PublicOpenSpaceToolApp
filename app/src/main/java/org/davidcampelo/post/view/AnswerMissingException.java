package org.davidcampelo.post.view;

import org.davidcampelo.post.model.Question;

/**
 * Created by davidcampelo on 8/29/16.
 */
public class AnswerMissingException extends Exception {
    Question question;
    public AnswerMissingException(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }
}
