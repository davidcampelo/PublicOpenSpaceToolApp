package org.davidcampelo.post.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by davidcampelo on 7/26/16.
 */
class PublicOpenSpaceDBHelper extends SQLiteOpenHelper{

    public PublicOpenSpaceDBHelper(Context context) {
        super(context, PublicOpenSpaceDBAdapter.TABLE_NAME, null, PublicOpenSpaceDBAdapter.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PublicOpenSpaceDBAdapter.TABLE_CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(this.getClass().getName(), "Upgrading database from version "+ oldVersion +" to version "+
                newVersion +", which will destroy all old data");

        // dropping table
        if (oldVersion == 1 && newVersion == 2) {
//            sqLiteDatabase.execSQL( "ALTER TABLE "+ PublicOpenSpaceDBAdapter.TABLE_NAME +" ADD COLUMN "+ PublicOpenSpaceDBAdapter.COLUMN_LATITUDE +" REAL NOT NULL" );
//            sqLiteDatabase.execSQL( "ALTER TABLE "+ PublicOpenSpaceDBAdapter.TABLE_NAME +" ADD COLUMN "+ PublicOpenSpaceDBAdapter.COLUMN_LONGITUDE +" REAL NOT NULL" );
            sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS "+ PublicOpenSpaceDBAdapter.TABLE_NAME );
            onCreate(sqLiteDatabase );
        }
    }
}
