package org.davidcampelo.post.view;

import java.util.ArrayList;

/**
 * Listener interface to be implemented by the UI to receive notifications
 * from the {@QuestionView} when an Option with disable_question_numbers is
 * checked or unchecked. If this Option is checked, disableQuestions() must
 * be called, otherwise enableQuestions() is called.
 */
public interface DisableQuestionNumbersListener {
    public void disableQuestions(ArrayList<String> disableQuestionNumbers);
    public void enableQuestions(ArrayList<String> disableQuestionNumbers);
}
