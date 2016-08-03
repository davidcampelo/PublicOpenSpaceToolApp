package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Base Data Access Object
 * Handles and abstracts connection with Database as well as its commands
 *
 * Created by davidcampelo on 8/3/16.
 */
public abstract class DAO {
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private DAOHelper dbHelper;

    public DAO(Context context) {
        this(context, null);
    }

    public DAO(Context context, DAOHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    public void open(){
        if (dbHelper == null)
            dbHelper = new DAOHelper(context);
        if (!dbHelper.isOpen())
            sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public void close(){
        if (dbHelper != null && dbHelper.isOpen()) {
            dbHelper.close();
        }
    }


    // Proxy methods to select/insert/update/delete items
    protected Cursor select(String tableName, String[] tableColumns, String whereClauses){
        return sqLiteDatabase.query(tableName, tableColumns, whereClauses, null, null, null, null);
    }

    protected long insert(String tableName, ContentValues values){
        return sqLiteDatabase.insert(tableName, null, values);
    }
    protected long update(String tableName, ContentValues values, String whereClauses){
        return sqLiteDatabase.update(tableName, values, whereClauses, null);
    }
    protected long delete(String tableName, String whereClauses) {
        return sqLiteDatabase.delete(tableName, whereClauses,null);
    }

    protected Context getContext() {
        return context;
    }

    protected DAOHelper getDbHelper() {
        return dbHelper;
    }
}
