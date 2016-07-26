package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Database adapter
 * @author David Campelo <david.campelo@gmail.com>
 */
public class PublicOpenSpaceDBAdapter {
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private PublicOpenSpaceDBHelper dbHelper;


    static final String DATABASE_NAME = "post.db";
    static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME = "tb_pos_publicopenspace";
    private static final String COLUMN_ID = "pos_id";
    private static final String COLUMN_NAME = "pos_name";
    private static final String COLUMN_ADDRESS = "pos_address";
    private static final String COLUMN_DATETIME = "pos_datetime";

    private static String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_ADDRESS,
            COLUMN_DATETIME
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_ID +" integer primary key autoincrement, "
            + COLUMN_NAME +" text not null,"
            + COLUMN_ADDRESS +" text not null,"
            + COLUMN_DATETIME +");";

    public PublicOpenSpaceDBAdapter(Context context) {
        this.context = context;
    }

    // open/close methods
    public void open(){
        dbHelper = new PublicOpenSpaceDBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    // DAO methods
    private PublicOpenSpace cursorToObject(Cursor cursor) {
        return new PublicOpenSpace(
                cursor.getLong(0),      // id
                cursor.getString(1),    // name
                cursor.getString(2),    // address
                cursor.getLong(3)       // dateCreation
        );
    }

    public PublicOpenSpace insert(PublicOpenSpace publicOpenSpace){
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, publicOpenSpace.name);
        values.put(COLUMN_ADDRESS, publicOpenSpace.address);
        values.put(COLUMN_DATETIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));

        publicOpenSpace.id = sqLiteDatabase.insert(TABLE_NAME, null, values);

        return publicOpenSpace;
    }

    public boolean update(PublicOpenSpace publicOpenSpace){
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, publicOpenSpace.name);
        values.put(COLUMN_ADDRESS, publicOpenSpace.address);

        return (sqLiteDatabase.update(TABLE_NAME, values,COLUMN_ID +"="+ publicOpenSpace.id , null) > 0);
    }

    public PublicOpenSpace get(long id) {
        if (id == 0)
            return new PublicOpenSpace();

        PublicOpenSpace object = null;

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, TABLE_COLUMNS,COLUMN_ID +"="+ id, null, null, null, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            object = cursorToObject(cursor);
        }

        cursor.close();

        return object;
    }

    public ArrayList<PublicOpenSpace> getAll() {
        ArrayList<PublicOpenSpace> list = new ArrayList<PublicOpenSpace>();

        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, TABLE_COLUMNS,null, null, null, null, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            list.add(cursorToObject(cursor));
        }

        cursor.close();

        return list;
    }

    public boolean delete(PublicOpenSpace publicOpenSpace){
        return (sqLiteDatabase.delete(TABLE_NAME, COLUMN_ID +"="+ publicOpenSpace.id,null) > 0);
    }

}
