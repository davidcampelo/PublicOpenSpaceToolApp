package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.davidcampelo.post.utils.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
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

    /**
     * Drop table and creates it again
     *
     * Implemented by sub-classes
     */
    protected abstract String getTableName();
    protected abstract String[] getTableColumns();
    protected abstract String getTableCreateCommand();

    public void resetData() {
        drop(getTableName());
        exec(getTableCreateCommand());

    }

    public void dump(FileWriter out) throws IOException {
        open();
        String tableName = getTableName();
        String[] tableColumns = getTableColumns();

        StringUtils.writeToFile(out, "DROP TABLE IF EXISTS "+ tableName+";");
        StringUtils.writeToFile(out, getTableCreateCommand());

        Cursor cursor = select(tableName, tableColumns, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            StringBuilder clause = new StringBuilder();
            clause.append("INSERT INTO "+ tableName +"(");
            for (String column : tableColumns) {
                clause.append(""+ column +",");
            }
            clause.deleteCharAt(clause.length()-1); // remove last comma
            clause.append(") ");
            clause.append("VALUES(");
            for (int i = 0; i < tableColumns.length; i++) {
                if ( cursor.getString(i) == null )
                    clause.append("null,");
                else
                    clause.append("'"+ cursor.getString(i).replace("'", "''") +"',");
            }
            clause.deleteCharAt(clause.length()-1); // remove last comma
            clause.append(");");

            StringUtils.writeToFile(out, clause.toString());
        }

        cursor.close();
        close();
    }

    // Proxy methods to select/insert/update/delete items
    protected void exec(String sql) {
        sqLiteDatabase.execSQL(sql);
    }
    protected void drop(String tableName) {
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS "+ tableName);
    }
    protected Cursor select(String tableName, String[] tableColumns, String whereClauses) {
//        Log.e(this.getClass().getName(), ">>>>>>> SELECT " + StringUtils.toString(tableColumns) +
//                " FROM " + tableName + " WHERE " + whereClauses);
        return sqLiteDatabase.query(tableName, tableColumns, whereClauses, null, null, null, null);
    }

    protected long insert(String tableName, ContentValues values) {
        Log.e(this.getClass().getName(), ">>>>>>> INSERT INTO " + tableName +
                "(" + StringUtils.toString2(values.keySet()) +
                ") VALUES(" + StringUtils.toString(values.valueSet()) + ")");

        return sqLiteDatabase.insert(tableName, null, values);
    }

    protected long update(String tableName, ContentValues values, String whereClauses) {
        Log.e(this.getClass().getName(), ">>>>>>> UPDATE " + tableName +
                "(" + StringUtils.toString2(values.keySet()) +
                ") VALUES(" + StringUtils.toString(values.valueSet()) + ") WHERE " + whereClauses);
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

}
