package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Question Database Access Object
 *
 * @author David Campelo <david.campelo@gmail.com>
 */
public class QuestionDAO extends DAO {


    static final String TABLE_NAME = "tb_qst_question";
    static final String COLUMN_NUMBER = "qst_number";
    static final String COLUMN_TITLE = "qst_title";
    static final String COLUMN_TYPE = "qst_type";

    static String[] TABLE_COLUMNS = {
            COLUMN_NUMBER,
            COLUMN_TITLE,
            COLUMN_TYPE
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_NUMBER +" TEXT primary key not null, "
            + COLUMN_TITLE +" TEXT not null, "
            + COLUMN_TYPE +" TEXT not null);";

    public QuestionDAO(Context context) {
        super(context);
    }

    public QuestionDAO(Context context, DAOHelper dbHelper, SQLiteDatabase sqLiteDatabase) {
        super(context, dbHelper, sqLiteDatabase);
    }

    public Question insert(Question object){

        ContentValues values = new ContentValues();

        values.put(COLUMN_NUMBER, object.number);
        values.put(COLUMN_TITLE, object.title);
        values.put(COLUMN_TYPE, object.type.name());

        insert(TABLE_NAME, values);

        return object;
    }

    public boolean update(Question object){

        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, object.title);
        values.put(COLUMN_NUMBER, object.number);
        values.put(COLUMN_TYPE, object.type.name());

        return (update(TABLE_NAME, values, COLUMN_NUMBER +"='"+ object.number +"'") > 0);
    }

    /**
     * Retrieves a complete Question object (aka with full Options) based on
     * question number and PublicOpenSpace object
     *
     * @param number
     * @param publicOpenSpace if PublicOpenSpace.id == 0 then we should retrieve all the "default"
     *                        options, otherwise we will retrieve "default" options + the user
     *                        previously inserted ones
     * @return
     */
    public Question get(String number, PublicOpenSpace publicOpenSpace) {

        Question object = null;
        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_NUMBER +"='"+ number +"'");

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            object = cursorToObject(cursor);
        }

        cursor.close();

        // retrieve options
        OptionDAO optionDAO = new OptionDAO(getContext(), getDbHelper(), getSqLiteDatabase());
        object.options = optionDAO.get(object, publicOpenSpace);

        return object;
    }

    public ArrayList<Question> getAll() {
        ArrayList<Question> list = new ArrayList<Question>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            list.add(cursorToObject(cursor));
        }

        cursor.close();

        return list;
    }

    public long delete(Question object){
        return delete(TABLE_NAME, COLUMN_NUMBER +"='"+ object.number+"'");
    }

    private Question cursorToObject(Cursor cursor) {
        return new Question(
                cursor.getString(0),                                // number
                cursor.getString(1),                                // title
                Question.QuestionType.valueOf(cursor.getString(2)), // type
                null,                                               // TODO: retrieve options
                null                                                // TODO: retrieve answers
        );
    }
}
