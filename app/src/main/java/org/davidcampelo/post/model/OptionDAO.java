package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Option Database Access Object
 *
 * @author David Campelo <david.campelo@gmail.com>
 */
public class OptionDAO extends DAO {


    static final String TABLE_NAME = "tb_opt_option";
    static final String COLUMN_ID = "opt_id";
    static final String COLUMN_ALIAS = "opt_alias";
    static final String COLUMN_VALUE = "opt_value";
    static final String COLUMN_TITLE = "opt_title";
    static final String COLUMN_QST_NUMBER = "qst_number"; // Question this option belongs to
    static final String COLUMN_POS_ID = "pos_id"; // if this Option concerns to one and only
    // PublicOpenSpace (happens for "Other" options)

    static String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_ALIAS,
            COLUMN_VALUE,
            COLUMN_TITLE,
            COLUMN_QST_NUMBER,
            COLUMN_POS_ID
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_ID +" INTEGER primary key autoincrement, "
            + COLUMN_ALIAS +" TEXT not null, "
            + COLUMN_VALUE +" TEXT not null, "
            + COLUMN_TITLE +" TEXT not null, "
            + COLUMN_QST_NUMBER +" TEXT, "
            + COLUMN_POS_ID +" INTEGER);";

    public OptionDAO(Context context) {
        super(context);
    }
    public OptionDAO(Context context, DAOHelper dbHelper, SQLiteDatabase sqLiteDatabase) {
        super(context, dbHelper, sqLiteDatabase);
    }

    public void resetData(){
        drop(TABLE_NAME);
        exec(TABLE_CREATE_CMD);
    }

    public Option insert(Option object){

        ContentValues values = new ContentValues();

        values.put(COLUMN_ALIAS, object.alias);
        values.put(COLUMN_VALUE, object.value);
        values.put(COLUMN_TITLE, object.title);
        values.put(COLUMN_QST_NUMBER, object.question.number);

        if (object.publicOpenSpace != null) {
            values.put(COLUMN_POS_ID, object.publicOpenSpace.id);
        }

        object.id = insert(TABLE_NAME, values);

        return object;
    }

    public boolean update(Option object){

        ContentValues values = new ContentValues();

        values.put(COLUMN_ALIAS, object.alias);
        values.put(COLUMN_VALUE, object.value);
        values.put(COLUMN_TITLE, object.title);
        values.put(COLUMN_QST_NUMBER, object.question.number);


        return (update(TABLE_NAME, values, COLUMN_ID +"="+ object.id) > 0);
    }

    /**
     * Retrieves all Option objects based on Question and PublicOpenSpace objects
     * @param question
     * @param publicOpenSpace if PublicOpenSpace.id == 0 then we should retrieve all the "default"
     *                        options, otherwise we will retrieve "default" options + the user
     *                        previously inserted ones
     * @return
     */
    public ArrayList<Option> getAll(Question question, PublicOpenSpace publicOpenSpace) {
        ArrayList<Option> list = new ArrayList<Option>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_QST_NUMBER +" = '"+ question.number +"' AND "+
                " ("+ COLUMN_POS_ID+" IS NULL OR "+COLUMN_POS_ID+" = "+publicOpenSpace.id+" ) "+
                "ORDER BY "+ COLUMN_ID +" DESC");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.add(cursorToObject(cursor));
        }

        cursor.close();

        return list;
    }

    public Option get(long id) {
        Option object = new Option(); // if no object was found, just return an empty object

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_ID +"="+ id);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            object = cursorToObject(cursor);
        }

        cursor.close();

        return object;
    }

    public ArrayList<Option> getAll() {
        ArrayList<Option> list = new ArrayList<Option>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.add(cursorToObject(cursor));
        }

        cursor.close();

        return list;
    }

//    public long delete(Option object){
//        return delete(TABLE_NAME, COLUMN_ID +"="+ object.id);
//    }

    private Option cursorToObject(Cursor cursor) {
        return new Option(
                cursor.getLong(0),      // id
                cursor.getString(1),    // alias
                cursor.getString(2),    // value
                cursor.getString(3),    // title
                null,                   // Question
                null                    // PublicOpenSpace
        );
    }
}
