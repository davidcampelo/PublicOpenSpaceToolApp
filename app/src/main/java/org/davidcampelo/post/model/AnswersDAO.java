package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Option Database Accesss Object
 *
 * @author David Campelo <david.campelo@gmail.com>
 */
public class AnswersDAO extends DAO {


    static final String TABLE_NAME = "tb_ans_answers";
    static final String COLUMN_ID1 = "pub_id";
    static final String COLUMN_ID2 = "qst_id";
    static final String COLUMN_ID3 = "opt_id";

    static String[] TABLE_COLUMNS = {
            COLUMN_ID1,
            COLUMN_ID2,
            COLUMN_ID3

    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_ID1 +" INTEGER, "
            + COLUMN_ID2 +" INTEGER, "
            + COLUMN_ID3 +" INTEGER);";

    public AnswersDAO(Context context) {
        super(context);
    }
}
