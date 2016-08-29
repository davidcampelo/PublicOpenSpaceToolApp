package org.davidcampelo.post.model;

import org.davidcampelo.post.utils.Constants;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by davidcampelo on 7/28/16.
 * {@deprecated}
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
        question_25, // Yes/No


        // QUESTION 26 - Is children\'s play equipment present?
        question_26, // Yes/No (go to Q30)

        // QUESTION 27 - What items of play equipment are present? (tick all relevant)
        question_27_1, // Swing/s
        question_27_2, // Slide/s
        question_27_3, // Climbing equipment
        question_27_4, // Hanging Bars/Rings
        question_27_5, // SeeSaws/Rockers
        question_27_6, // Bridges/Tunnels
        question_27_7, // Activity Panels (e.g. noughts &amp; crosses)
        question_27_8, // Cubby House/s
        question_27_9, // Other (specify)

        // QUESTION 28 - What is the playground surface? (tick all relevant)
        question_28_1, // Sand
        question_28_2, // Grass
        question_28_3, // Rubber
        question_28_4, // Gravel or pebbles
        question_28_5, // Woodchips
        question_28_6, // Other (specify)


        // QUESTION 29 - Is playground shaded? (tick one only)
        question_29_1, // Partial cover/shade
        question_29_2, // Total cover/shade
        question_29_3, // No cover/shade


        // QUESTION 30 - Are barbecues present?
        question_30, // Yes/No


        // QUESTION 31 - Are picnic tables present?
        question_31, // Yes/No

        // QUESTION 32a - Are the parking facilities serving the P.O.S.?
        question_32a, // Yes/No

        // QUESTION 32b - Estimate the number of bays
        question_32b_1, // 0-20
        question_32b_2, // 21-50
        question_32b_3, // More than 50

        // QUESTION 33 - Are the public access toilets?
        question_33, // Yes/No

        // QUESTION 34 - Is there a kiosk/café present? (tick one only)
        question_34_1, // 7 days per week
        question_34_2, // Weekdays only
        question_34_3, // Weekends only
        question_34_4, // No

        // QUESTION 35 - Is there access to public transport within one block of P.O.S.?
        question_35, // Yes/No

        // QUESTION 36 - Is there seating present?
        question_36, // Yes/No

        // QUESTION 37 - Are there clubrooms/meeting rooms present?
        question_37, // Yes/No

        // QUESTION 38 - Are rubbish bins present?
        question_38, // Yes/No

        // QUESTION 39 - Are dog litter bags provided?
        question_39, // Yes/No

        // QUESTION 40 - In how many locations in the P.O.S. are dog litter bags present?
        question_40,

        // QUESTION 41 - Are there taps or other water sources accessible for dogs?
        question_41, // Yes/No

        // QUESTION 42 - Are drinking fountains present?
        question_42, // Yes/No

        // QUESTION 43 - Is the lighting within the P.O.S.? (i.e., not just street lighting)
        question_43, // Yes/No (go to Q45)

        // QUESTION 44 - Where is the lighting located? (tick all relevant)
        question_44_1, // Around courts, buildings, and equipment
        question_44_2, // Along paths
        question_44_3, // Perimeter all sides
        question_44_4, // Perimeter some sides
        question_44_5, // Random throughout P.O.S.

        // QUESTION 45 - From the centre of the P.O.S., how many visible are surrounding roads? (tick one)
        question_45_1, // Road/s clearly visible from the centre of the P.O.S.
        question_45_2, // Road/s partly visible from the centre of the P.O.S.
        question_45_3, // Road/s cannot be seen from the centre of the P.O.S.

        // QUESTION 46.a - From the centre of the P.O.S., how visible are the surrounding houses? Clear visibility means you can clearly see windows, back, yards, or front yards of houses overlooking the park. (tick one)
        question_46a_1, // House/s clearly visible from the centre of the P.O.S.
        question_46a_2, // House/s partly visible from the centre of the P.O.S.
        question_46a_3, // House/s cannot be seen from the centre of the P.O.S. (go to Q47)

        // QUESTION 46.b - How many of these houses overlook the park? (tick one)
        question_46b_1, // More than 10
        question_46b_2, // Between 5 and 10
        question_46b_3, // Between 1 and 5

        // QUESTION 47 - Are all roads surrounding the P.O.S. minor roads or cul-de-sacs?
        question_47, // Yes/No

        // QUESTION 48.a - Does the major roads/s have a zebra crossing to assist access to the P.O.S.?
        question_48a, // Yes/No

        // QUESTION 48.b - Does the major roads/s have a pedestrian crossing with signals to assist access to the P.O.S.?
        question_48b, // Yes/No

        // QUESTION 49 - To what extent do you agree or disagree with each of the following statements regarding this P.O.S.? (choose one number fo each item)
        // Possible answers for each of QUESTION 49:
        //            1 = Strongly agree
        //            2 = Agree
        //            3 = Neither Agree nor Disagree
        //            4 = Disagree
        //            5 = Strongly disagree
        question_49a, // P.O.S. is interesting for walking
        question_49b, // P.O.S. is suitable for casual ball sports
        question_49c  // P.O.S. is suitable for cycling
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
            StringTokenizer tokenizer = new StringTokenizer(questionsString, Constants.QUESTION_ANSWERS_SEPARATOR);

            for (int i = -1; ++i < identifiers.length && tokenizer.hasMoreElements(); ) {
                questions.putAnswer(identifiers[i], (String) tokenizer.nextElement());
            }
        }
        return questions;
    }

    /**
     * Get a String representing all answers separated by {@link Constants}
     */
    public String getAnswers(){
        StringBuilder stringBuilder = new StringBuilder();
        QuestionIdentifier[] identifiers = QuestionIdentifier.values();
        for (int i = -1; ++i < identifiers.length;) {
            stringBuilder.append(answers.get(identifiers[i]));
            if (i < identifiers.length - 1)
                stringBuilder.append(Constants.QUESTION_ANSWERS_SEPARATOR);
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
