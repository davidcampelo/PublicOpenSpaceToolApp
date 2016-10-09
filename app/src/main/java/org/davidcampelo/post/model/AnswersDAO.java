package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

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
            COLUMN_ANSWER_TEXT,
            COLUMN_QST_NUMBER
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE " + TABLE_NAME + " ( "
            + COLUMN_POS_ID + " INTEGER not null, "
            + COLUMN_QST_NUMBER + " TEXT not null, "
            + COLUMN_ANSWER_TEXT + " TEXT not null);";

    public AnswersDAO(Context context) {
        super(context);
    }


    public void resetData(){
        drop(TABLE_NAME);
        exec(TABLE_CREATE_CMD);
    }

    /**
     * Retrieves the answer of a given question in a given PublicOpenSpace
     *
     * @param question
     * @param publicOpenSpace
     * @return
     */
    public String get(Question question, PublicOpenSpace publicOpenSpace) {
        String answers = "";

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS_ANSWERS, COLUMN_QST_NUMBER + " = '" + question.number + "' AND " +
                " " + COLUMN_POS_ID + " = " + publicOpenSpace.id + " ");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            answers = cursor.getString(0);
        }

        cursor.close();

        return answers;
    }
    /**
     * Retrieves HashMap<Question number, answers> with all answers of all questions for a given PublicOpenSpace
     *
     * @param publicOpenSpace
     * @return
     */
    public HashMap<String, String> getAll(PublicOpenSpace publicOpenSpace) {
        HashMap<String, String> answers = new HashMap<>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS_ANSWERS, COLUMN_POS_ID + " = " + publicOpenSpace.id +
                " ORDER BY " + COLUMN_QST_NUMBER + " ASC");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            answers.put(cursor.getString(1), cursor.getString(0));
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