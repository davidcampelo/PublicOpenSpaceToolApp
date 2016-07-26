package org.davidcampelo.post.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by davidcampelo on 7/26/16.
 */
public class PublicOpenSpaceDBHelper extends SQLiteOpenHelper{

    public PublicOpenSpaceDBHelper(Context context, PublicOpenSpaceDBAdapter dbAdadpter) {
        super(context, PublicOpenSpaceDBAdapter.TABLE_NAME, null, PublicOpenSpaceDBAdapter.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
