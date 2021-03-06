package org.davidcampelo.post.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.davidcampelo.post.utils.Constants;


/**
 * Created by davidcampelo on 7/26/16.
 */
public final class DAOHelper extends SQLiteOpenHelper{

    private boolean open = false;
    private Context context;

    public DAOHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ProjectDAO.TABLE_CREATE_CMD);
        sqLiteDatabase.execSQL(PublicOpenSpaceDAO.TABLE_CREATE_CMD);
        sqLiteDatabase.execSQL(QuestionDAO.TABLE_CREATE_CMD);
        sqLiteDatabase.execSQL(OptionDAO.TABLE_CREATE_CMD);
        sqLiteDatabase.execSQL(AnswersDAO.TABLE_CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(this.getClass().getName(), "Upgrading database from version "+ oldVersion +" to version "+
                newVersion +", which will destroy all old data...");

        // XXX hacking to HINT
       // dropping table
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS "+ ProjectDAO.TABLE_NAME );
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS "+ PublicOpenSpaceDAO.TABLE_NAME );
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS "+ QuestionDAO.TABLE_NAME );
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS "+ OptionDAO.TABLE_NAME );
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS "+ AnswersDAO.TABLE_NAME );
        onCreate(sqLiteDatabase );
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public synchronized void close() {
        open = false;
        super.close();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        open = true;
        return super.getWritableDatabase();
    }

}
