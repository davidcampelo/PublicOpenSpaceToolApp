package org.davidcampelo.post.model;

import com.google.android.gms.games.quest.Quest;

import org.davidcampelo.post.utils.Constants;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by davidcampelo on 7/28/16.
 */
// QUESTIONS
public class Questions {

    public enum QuestionIdentifier {
        // QUESTION 7 - Type of usage (tick all relevant)
        question_7_1, // Active-formal
        question_7_2, // Active-informal
        question_7_3, // Passive only (go to Q9)

        // QUESTION 8 - For what type of activities the space is designed? (tick all relevant)
        question_8_1, // Tennis (grass/hard courts)
        question_8_2, // Soccer
        question_8_3, // Football
        question_8_4, // Netball (grass/hard courts
        question_8_5, // Cricket
        question_8_6, // Baseball
        question_8_7, // Walking (only if paths)
        question_8_8, // Cycling
        question_8_9, // Fitness circuit
        question_8_10, // Basketball/Netball Hoops
        question_8_11, // Hockey
        question_8_12, // Athletics
        question_8_13, // Rugby
        question_8_14, // Children\'s playground
        question_8_15, // Other

        // QUESTION 9 - Is the P.O.S. on the beach/river foreshore?
        question_9, // Yes/No

        // QUESTION 10 - Are there water features in the P.O.S.?
        question_10, // Yes/No (Go to Q13)

        // QUESTION 11 - Type of water feature (tick all relevant)
        question_11_1, // Lake
        question_11_2, // Pond
        question_11_3, // Water fountain
        question_11_4, // Stream
        question_11_5, // Other

        // QUESTION 12 - Estimate the percentage of the P.O.S. occupied by the water feature(s) (tick one)
        question_12, // 1) Up to 25%, 2) 26% - 50%, 3) 51% - 75% 4) More than 75%

        // QUESTION 13a - Are there other aesthetic features in the P.O.S.?   `
        question_13a, // Yes/No (Go to Q14)                                `

        // QUESTION 13b - Which of the following features are present? (tick all relevant)
        question_13b_1, // Statues
        question_13b_2, // Gazebos/Rotunda
        question_13b_3, // Sculptures
        question_13b_4, // Ducks/Swans
        question_13b_5, // Bridge
        question_13b_6, // Rocks
        question_13b_7, // Other

        // QUESTION 14 - Are there trees in the P.O.S.?   `
        question_14, // Yes/No (Go to Q18)                                `

        // QUESTION 15 - Estimate the approximate number of trees present (tick one)
        question_15, // 1) 1 - 50 trees 2) 50 - 100 trees 3) More than 100 trees

        // QUESTION 16 - Where are the trees placed? (tick all relevant)
        question_16_1, // Perimeter all sides
        question_16_2, // Perimeter some sides
        question_16_3, // Along walking paths
        question_16_4, // Random placement throughout
        question_16_5, // Other

        // QUESTION 17 - Are there gardens in this P.O.S.?   `
        question_17, // Yes/No                                `

        // QUESTION 18a - Are the walking paths or cycleways within or around the P.O.S.? (tick all relevant)
        question_18a_1, // Walking path/s
        question_18a_2, // Designated dual use path/s
        question_18a_3, // None

        // QUESTION 18b - Shade along paths (tick one)
        question_18b, // 1) Very good (canopies of many trees touch)
        // 2) Good (canopies of some trees touch)
        // 3) Medium (canopies don\'t touch but trees close together)
        // 4) Poor (canopies of trees don\'t touch and trees spread apart)
        // 5) Very poor (little or no shade)

        // QUESTION 19 - Describe the placement of paths within the P.O.S. (tick all relevant)
        question_19_1, // Perimeter, all sides
        question_19_2, // Perimeter, some sides
        question_19_3, // Diagonal
        question_19_4, // Radial
        question_19_5, // Path around water/visual feature
        question_19_6, // Other

        // QUESTION 20 - Is there evidence that the grass is watered?   `
        question_20, // Yes/No

        // QUESTION 21 - Are dogs allowed? (tick all relevant)
        question_21_1, // Yes, on leash at all times
        question_21_2, // Yes, on leash at certain times
        question_21_3, // Yes, no leash specified
        question_21_4, // Not allowed (go to Q23)
        question_21_5, // Not specified (go to Q23)

        // QUESTION 22 - Is access for dogs: (tick one)
        question_22, // 1) Restricted in some areas
        // 2) Allowed all areas
        // 3) Not specified

        // QUESTION 23 - Is graffiti present?   `
        question_23, // Yes/No                                `

        // QUESTION 24 - Is vandalism evident?   `
        question_24, // Yes/No                                `

        // QUESTION 25 - Is there litter throughout the P.O.S.?   `
        question_25 // Yes/No
    }

    protected HashMap<QuestionIdentifier, String> answers;

    /**
     * Force factory method to create Questions objects
     */
    private Questions(){
        answers = new HashMap<>();
    }

    /**
     * Factory method to build up a new Questions object
     *
     * This method return an empty object (all answers empty)
     * if the parameter questionsString is null or is zero-length
     */
    public static Questions parse(String questionsString){
        Questions questions = new Questions();
        // fill all questions based on a comma separated String
        QuestionIdentifier[] identifiers = QuestionIdentifier.values();
        if (questionsString == null || questionsString.length() == 0) {
            for (int i = -1; ++i < identifiers.length;){
                questions.putAnswer(identifiers[i], ""); // fill with empty answers
            }
        }
        else {
            StringTokenizer tokenizer = new StringTokenizer(questionsString);

            for (int i = -1; ++i < identifiers.length && tokenizer.hasMoreElements(); ) {
                questions.putAnswer(identifiers[i], (String) tokenizer.nextElement());
            }
        }
        return questions;
    }

    /**
     * Get a String representing all answers separated by {@link Constants.QUESTIONS_SEPARATOR}
     */
    public String getAnswers(){
        StringBuilder stringBuilder = new StringBuilder();
        QuestionIdentifier[] identifiers = QuestionIdentifier.values();
        for (int i = -1; ++i < identifiers.length;) {
            stringBuilder.append(answers.get(identifiers[i]));
            if (i < identifiers.length - 1)
                stringBuilder.append(Constants.QUESTIONS_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    /**
     * Insert into the HASHMAP the answer for a given question
     */
    public void putAnswer(QuestionIdentifier identifier, String value){
        answers.put(identifier, value);
    }

    public String getAnswer(QuestionIdentifier identifier){
        return answers.get(identifier);
    }
}
