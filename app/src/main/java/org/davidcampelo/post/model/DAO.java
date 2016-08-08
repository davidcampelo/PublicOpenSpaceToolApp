package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
        this(context, null, null);
    }

    public DAO(Context context, DAOHelper dbHelper, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void open() {
        if (dbHelper == null)
            dbHelper = new DAOHelper(context);
        if (!dbHelper.isOpen())
            sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (dbHelper != null && dbHelper.isOpen()) {
            dbHelper.close();
        }
    }

    // Proxy methods to select/insert/update/delete items
    protected Cursor select(String tableName, String[] tableColumns, String whereClauses) {
        Log.e(this.getClass().getName(), ">>>>>>> SELECT " + toString(tableColumns) + " FROM " + tableName + " WHERE " + whereClauses);
        return sqLiteDatabase.query(tableName, tableColumns, whereClauses, null, null, null, null);
    }

    protected long insert(String tableName, ContentValues values) {
        Log.e(this.getClass().getName(), ">>>>>>> INSERT INTO " + tableName + "(" + toString2(values.keySet()) + ") VALUES(" + toString3(values.valueSet()) + ")");

        return sqLiteDatabase.insert(tableName, null, values);
    }

    protected long update(String tableName, ContentValues values, String whereClauses) {
        Log.e(this.getClass().getName(), ">>>>>>> UPDATE " + tableName + "(" + toString2(values.keySet()) + ") VALUES(" + toString3(values.valueSet()) + ") WHERE " + whereClauses);
        return sqLiteDatabase.update(tableName, values, whereClauses, null);
    }

    protected long delete(String tableName, String whereClauses) {
        return sqLiteDatabase.delete(tableName, whereClauses, null);
    }

    protected Context getContext() {
        return context;
    }

    protected DAOHelper getDbHelper() {
        return dbHelper;
    }

    protected SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    private String toString(String[] array) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            b.append(array[i]);
            if (i < array.length - 1)
                b.append(", ");
        }

        return b.toString();
    }

    private String toString2(Set<String> strings) {
        StringBuilder b = new StringBuilder();
        Iterator<String> i = strings.iterator();
        while (i.hasNext()){
            b.append(i.next()+ ", ");
        }
        return b.toString();
    }

    private String toString3(Set<Map.Entry<String, Object>> entries) {
        StringBuilder b = new StringBuilder();
        Iterator<Map.Entry<String, Object>> i = entries.iterator();
        while (i.hasNext()){
            Map.Entry<String, Object> entry = i.next();
            b.append(entry.getValue()+ ", ");
        }
        return b.toString();
    }


}
