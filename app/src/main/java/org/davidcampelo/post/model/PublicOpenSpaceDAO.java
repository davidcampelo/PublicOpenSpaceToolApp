package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * PublicOpenSpace Database Accesss Object
 *
 * @author David Campelo <david.campelo@gmail.com>
 */
public class PublicOpenSpaceDAO extends DAO {


    static final String TABLE_NAME = "tb_pos_publicopenspace";
    static final String COLUMN_ID = "pos_id";
    static final String COLUMN_NAME = "pos_name";
    static final String COLUMN_DATETIME = "pos_datetime";

    static String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_DATETIME
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_ID +" INTEGER primary key autoincrement, "
            + COLUMN_NAME +" TEXT not null,"
            + COLUMN_DATETIME +");";

    public PublicOpenSpaceDAO(Context context) {
        super(context);
    }

    public PublicOpenSpace insert(PublicOpenSpace publicOpenSpace){

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, publicOpenSpace.name);
        values.put(COLUMN_DATETIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));

        publicOpenSpace.id = insert(TABLE_NAME, values);

        return publicOpenSpace;
    }

    public boolean update(PublicOpenSpace publicOpenSpace){

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, publicOpenSpace.name);

        return (update(TABLE_NAME, values, COLUMN_ID +"="+ publicOpenSpace.id) > 0);
    }

    public PublicOpenSpace get(long id) {
        PublicOpenSpace object = new PublicOpenSpace(); // if no object was found, just return an empty object

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_ID +"="+ id);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            object = cursorToObject(cursor);
        }

        cursor.close();

        return object;
    }

    public ArrayList<PublicOpenSpace> getAll() {
        ArrayList<PublicOpenSpace> list = new ArrayList<PublicOpenSpace>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            list.add(cursorToObject(cursor));
        }

        cursor.close();

        return list;
    }

    public boolean delete(PublicOpenSpace publicOpenSpace){
        return (delete(TABLE_NAME, COLUMN_ID +"="+ publicOpenSpace.id) > 0);
    }

    public static PublicOpenSpace staticGet(Context ctx, long id) {
        PublicOpenSpace object;
        PublicOpenSpaceDAO dbAdapter = new PublicOpenSpaceDAO(ctx);
        dbAdapter.open();
        object = dbAdapter.get(id);
        dbAdapter.close();
        return object;
    }
    public static PublicOpenSpace staticInsert(Context ctx, PublicOpenSpace object) {
        PublicOpenSpaceDAO dbAdapter = new PublicOpenSpaceDAO(ctx);
        dbAdapter.open();
        object = dbAdapter.insert(object);
        dbAdapter.close();

        return object;
    }
    public static void staticUpdate(Context ctx, PublicOpenSpace object) {
        PublicOpenSpaceDAO dbAdapter = new PublicOpenSpaceDAO(ctx);
        dbAdapter.open();
        dbAdapter.update(object);
        dbAdapter.close();
    }
    public static void staticDelete(Context ctx, PublicOpenSpace object) {
        PublicOpenSpaceDAO dbAdapter = new PublicOpenSpaceDAO(ctx);
        dbAdapter.open();
        dbAdapter.delete(object);
        dbAdapter.close();
    }

    private PublicOpenSpace cursorToObject(Cursor cursor) {
        return new PublicOpenSpace(
                cursor.getLong(0),      // id
                cursor.getString(1),    // name
                cursor.getLong(2)       // dateCreation
        );
    }
}
