package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Option Database Access Object
 *
 * @author David Campelo <david.campelo@gmail.com>
 */
public class AnswersDAO extends DAO {


    static final String TABLE_NAME = "tb_ans_answers";
    static final String COLUMN_POS_ID = "pos_id";
    static final String COLUMN_QST_NUMBER = "qst_number";
    static final String COLUMN_ANSWER_TEXT = "ans_text";

    static String[] TABLE_COLUMNS = {
            COLUMN_POS_ID,
            COLUMN_QST_NUMBER,
            COLUMN_ANSWER_TEXT

    };

    static String[] TABLE_COLUMNS_ANSWERS = {
            COLUMN_ANSWER_TEXT

    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE " + TABLE_NAME + " ( "
            + COLUMN_POS_ID + " INTEGER not null, "
            + COLUMN_QST_NUMBER + " TEXT not null, "
            + COLUMN_ANSWER_TEXT + " TEXT not null);";

    public AnswersDAO(Context context) {
        super(context);
    }

    /**
     * Retrieves all Option objects based on Question and PublicOpenSpace objects
     *
     * @param question
     * @param publicOpenSpace if PublicOpenSpace.id == 0 then we should retrieve all the "default"
     *                        options, otherwise we will retrieve "default" options + the user
     *                        previously inserted ones
     * @return
     */
    public String get(Question question, PublicOpenSpace publicOpenSpace) {
        String answers = "";

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS_ANSWERS, COLUMN_QST_NUMBER + " = '" + question.number + "' AND " +
                " " + COLUMN_POS_ID + " = " + publicOpenSpace.id + " ");

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            answers = cursor.getString(0);
        }

        cursor.close();

        return answers;
    }

    public void insert(PublicOpenSpace object, Question question, String answers) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_POS_ID, object.id);
        values.put(COLUMN_QST_NUMBER, question.number);
        values.put(COLUMN_ANSWER_TEXT, answers);

        insert(TABLE_NAME, values);
    }

    public void delete(PublicOpenSpace publicOpenSpace) {
        delete(TABLE_NAME, COLUMN_POS_ID +" = "+ publicOpenSpace.id);
    }
}