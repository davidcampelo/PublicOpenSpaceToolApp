package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.davidcampelo.post.utils.MapUtility;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Project Database Access Object
 *
 * @author David Campelo <david.campelo@gmail.com>
 */
public class ProjectDAO extends DAO {


    static final String TABLE_NAME = "tb_prj_publicopenspace";
    static final String COLUMN_ID = "prj_id";
    static final String COLUMN_NAME = "prj_name";
    static final String COLUMN_DESC = "prj_desc";
    static final String COLUMN_POINTS = "prj_points";
    static final String COLUMN_DATETIME = "prj_datetime";

    static String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_DESC,
            COLUMN_POINTS,
            COLUMN_DATETIME
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_ID +" INTEGER primary key autoincrement, "
            + COLUMN_NAME +" TEXT not null,"
            + COLUMN_DESC +" TEXT not null,"
            + COLUMN_POINTS +" TEXT not null,"
            + COLUMN_DATETIME +");";

    public ProjectDAO(Context context) {
        super(context);
    }

    public void resetData(){
        drop(TABLE_NAME);
        exec(TABLE_CREATE_CMD);
    }

    public Project insert(Project project){

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, project.name);
        values.put(COLUMN_DESC, project.desc);
        values.put(COLUMN_POINTS, MapUtility.parsePoints(project.polygonPoints));
        values.put(COLUMN_DATETIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));

        project.id = insert(TABLE_NAME, values);

        return project;
    }

    public boolean update(Project project){

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, project.name);
        values.put(COLUMN_DESC, project.desc);
        values.put(COLUMN_POINTS, MapUtility.parsePoints(project.polygonPoints));

        return (update(TABLE_NAME, values, COLUMN_ID +"="+ project.id) > 0);
    }

    public Project get(long id) {
        Project object = null;

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_ID +"="+ id);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            object = cursorToObject(cursor);
        }

        cursor.close();

        return object;
    }

    public ArrayList<Project> getAll() {
        ArrayList<Project> list = new ArrayList<Project>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.add(cursorToObject(cursor));
        }

        cursor.close();

        return list;
    }

    public boolean delete(Project project){
        return (delete(TABLE_NAME, COLUMN_ID +"="+ project.id) > 0);
    }

    public static Project staticGet(Context ctx, long id) {
        Project object;
        ProjectDAO dbAdapter = new ProjectDAO(ctx);
        dbAdapter.open();
        object = dbAdapter.get(id);
        dbAdapter.close();
        return object;
    }
    public static Project staticInsert(Context ctx, Project object) {
        ProjectDAO dbAdapter = new ProjectDAO(ctx);
        dbAdapter.open();
        object = dbAdapter.insert(object);
        dbAdapter.close();

        return object;
    }
    public static void staticUpdate(Context ctx, Project object) {
        ProjectDAO dbAdapter = new ProjectDAO(ctx);
        dbAdapter.open();
        dbAdapter.update(object);
        dbAdapter.close();
    }
    public static void staticDelete(Context ctx, Project object) {
        ProjectDAO dbAdapter = new ProjectDAO(ctx);
        dbAdapter.open();
        dbAdapter.delete(object);
        dbAdapter.close();
    }

    private Project cursorToObject(Cursor cursor) {
        return new Project(
                cursor.getLong(0),                                      // id
                cursor.getString(1),                                    // name
                cursor.getString(2),                                    // desc
                MapUtility.parsePoints(cursor.getString(3)),            // polygonPoints
                null,                                                   // publicOpenSpaces
                cursor.getLong(4)                                       // dateCreation
        );
    }
}
