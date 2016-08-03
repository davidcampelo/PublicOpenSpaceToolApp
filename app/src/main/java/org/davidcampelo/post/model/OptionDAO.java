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
public class OptionDAO extends DAO {


    static final String TABLE_NAME = "tb_opt_option";
    static final String COLUMN_ID = "opt_id";
    static final String COLUMN_TEXT = "opt_text";
    static final String COLUMN_POS_ID = "pos_id"; // if this Option concerns to one and only
    // PublicOpenSpace (happens for "Other" options)

    static String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_TEXT,
            COLUMN_POS_ID
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_ID +" INTEGER primary key autoincrement, "
            + COLUMN_TEXT +" TEXT not null, "
            + COLUMN_POS_ID +" INTEGER);";

    public OptionDAO(Context context) {
        super(context);
    }

    public Option insert(Option object){

        ContentValues values = new ContentValues();

        values.put(COLUMN_TEXT, object.text);

        object.id = insert(TABLE_NAME, values);

        return object;
    }

    public boolean update(Option object){

        ContentValues values = new ContentValues();

        values.put(COLUMN_TEXT, object.text);

        return (update(TABLE_NAME, values, COLUMN_ID +"="+ object.id) > 0);
    }

    public Option get(long id) {
        Option object = new Option(); // if no object was found, just return an empty object

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_ID +"="+ id);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            object = cursorToObject(cursor);
        }

        cursor.close();

        return object;
    }

    public ArrayList<Option> getAll() {
        ArrayList<Option> list = new ArrayList<Option>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            list.add(cursorToObject(cursor));
        }

        cursor.close();

        return list;
    }

    public long delete(Option object){
        return delete(TABLE_NAME, COLUMN_ID +"="+ object.id);
    }

    private Option cursorToObject(Cursor cursor) {
        return new Option(
                cursor.getLong(0),      // id
                cursor.getString(1),    // text
                false                   // checked
        );
    }
}
