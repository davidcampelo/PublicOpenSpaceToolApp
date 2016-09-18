package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.davidcampelo.post.utils.Constants;
import org.davidcampelo.post.utils.MapUtility;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * PublicOpenSpace Database Access Object
 *
 * @author David Campelo <david.campelo@gmail.com>
 */
public class PublicOpenSpaceDAO extends DAO {


    static final String TABLE_NAME = "tb_pos_publicopenspace";
    static final String COLUMN_ID = "pos_id";
    static final String COLUMN_NAME = "pos_name";
    static final String COLUMN_TYPE = "pos_type";
    static final String COLUMN_POINTS = "pos_points";
    static final String COLUMN_PROJECT_ID = "prj_id";
    static final String COLUMN_DATETIME = "pos_datetime";

    static String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_TYPE,
            COLUMN_POINTS,
            COLUMN_PROJECT_ID,
            COLUMN_DATETIME
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_ID +" INTEGER primary key autoincrement, "
            + COLUMN_NAME +" TEXT not null,"
            + COLUMN_TYPE +" TEXT not null,"
            + COLUMN_POINTS +" TEXT not null,"
            + COLUMN_PROJECT_ID +" INTEGER NOT NULL,"
            + COLUMN_DATETIME +");";

    public PublicOpenSpaceDAO(Context context) {
        super(context);
    }

    public void resetData(){
        drop(TABLE_NAME);
        exec(TABLE_CREATE_CMD);
    }

    public PublicOpenSpace insert(PublicOpenSpace publicOpenSpace){

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, publicOpenSpace.name);
        values.put(COLUMN_TYPE, publicOpenSpace.type.name());
        values.put(COLUMN_POINTS, MapUtility.parsePoints(publicOpenSpace.polygonPoints));
        values.put(COLUMN_PROJECT_ID, publicOpenSpace.project.id);
        values.put(COLUMN_DATETIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));

        publicOpenSpace.id = insert(TABLE_NAME, values);

        return publicOpenSpace;
    }

    public boolean update(PublicOpenSpace publicOpenSpace){

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, publicOpenSpace.name);
        values.put(COLUMN_TYPE, publicOpenSpace.type.name());
        values.put(COLUMN_POINTS, MapUtility.parsePoints(publicOpenSpace.polygonPoints));

        return (update(TABLE_NAME, values, COLUMN_ID +"="+ publicOpenSpace.id) > 0);
    }

    public PublicOpenSpace get(long id) {
        PublicOpenSpace object = null;

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_ID +"="+ id);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            object = cursorToObject(cursor);
        }

        cursor.close();

        return object;
    }

    public ArrayList<PublicOpenSpace> getAll() {
        ArrayList<PublicOpenSpace> list = new ArrayList<PublicOpenSpace>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.add(cursorToObject(cursor));
        }

        cursor.close();

        return list;
    }

    public ArrayList<PublicOpenSpace> getAllByProject(Project project) {
        ArrayList<PublicOpenSpace> list = new ArrayList<PublicOpenSpace>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_PROJECT_ID +"= "+ project.id);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            PublicOpenSpace object = cursorToObject(cursor);
            object.project = project;
            list.add(object);
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
                cursor.getLong(0),                                      // id
                cursor.getString(1),                                    // name
                PublicOpenSpace.Type.valueOf(cursor.getString(2)),      // type
                MapUtility.parsePoints(cursor.getString(3)),            // polygonPoints
                new Project(cursor.getLong(4)),                         // Project id
                cursor.getLong(5)                                       // dateCreation
        );
    }
}
