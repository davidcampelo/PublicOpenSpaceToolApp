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
    static final int DATABASE_VERSION = 2;
    static final String TABLE_NAME = "tb_pos_publicopenspace";
    static final String COLUMN_ID = "pos_id";
    static final String COLUMN_NAME = "pos_name";
    static final String COLUMN_ADDRESS = "pos_address";
    static final String COLUMN_LATITUDE = "pos_latitude";
    static final String COLUMN_LONGITUDE = "pos_longitude";
    static final String COLUMN_DATETIME = "pos_datetime";

    static String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_ADDRESS,
            COLUMN_LATITUDE,
            COLUMN_LONGITUDE,
            COLUMN_DATETIME
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_ID +" INTEGER primary key autoincrement, "
            + COLUMN_NAME +" TEXT not null,"
            + COLUMN_ADDRESS +" TEXT not null,"
            + COLUMN_LATITUDE +" REAL not null,"
            + COLUMN_LONGITUDE +" REAL not null,"
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
                cursor.getDouble(3),    // latitude
                cursor.getDouble(4),    // longitude
                cursor.getLong(5)       // dateCreation
        );
    }

    public PublicOpenSpace insert(PublicOpenSpace publicOpenSpace){
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, publicOpenSpace.name);
        values.put(COLUMN_ADDRESS, publicOpenSpace.address);
        values.put(COLUMN_LATITUDE, publicOpenSpace.latitude);
        values.put(COLUMN_LONGITUDE, publicOpenSpace.longitude);
        values.put(COLUMN_DATETIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));

        publicOpenSpace.id = sqLiteDatabase.insert(TABLE_NAME, null, values);

        return publicOpenSpace;
    }

    public boolean update(PublicOpenSpace publicOpenSpace){
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, publicOpenSpace.name);
        values.put(COLUMN_ADDRESS, publicOpenSpace.address);
        values.put(COLUMN_LATITUDE, publicOpenSpace.latitude);
        values.put(COLUMN_LONGITUDE, publicOpenSpace.longitude);

        return (sqLiteDatabase.update(TABLE_NAME, values,COLUMN_ID +"="+ publicOpenSpace.id , null) > 0);
    }

    public PublicOpenSpace get(long id) {
        PublicOpenSpace object = new PublicOpenSpace(); // if no object was found, just return an empty object

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

    public static PublicOpenSpace staticGet(Context ctx, long id) {
        PublicOpenSpace object;
        PublicOpenSpaceDBAdapter dbAdapter = new PublicOpenSpaceDBAdapter(ctx);
        dbAdapter.open();
        object = dbAdapter.get(id);
        dbAdapter.close();
        return object;
    }
    public static PublicOpenSpace staticInsert(Context ctx, PublicOpenSpace object) {
        PublicOpenSpaceDBAdapter dbAdapter = new PublicOpenSpaceDBAdapter(ctx);
        dbAdapter.open();
        object = dbAdapter.insert(object);
        dbAdapter.close();

        return object;
    }
    public static void staticUpdate(Context ctx, PublicOpenSpace object) {
        PublicOpenSpaceDBAdapter dbAdapter = new PublicOpenSpaceDBAdapter(ctx);
        dbAdapter.open();
        dbAdapter.update(object);
        dbAdapter.close();
    }
    public static void staticDelete(Context ctx, PublicOpenSpace object) {
        PublicOpenSpaceDBAdapter dbAdapter = new PublicOpenSpaceDBAdapter(ctx);
        dbAdapter.open();
        dbAdapter.delete(object);
        dbAdapter.close();
    }

}
