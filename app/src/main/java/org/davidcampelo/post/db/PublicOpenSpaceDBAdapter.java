package org.davidcampelo.post.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database adapter
 * @author David Campelo <david.campelo@gmail.com>
 */
public class PublicOpenSpaceDBAdapter {

    static final String DATABASE_NAME = "post.db";
    static final int DATABASE_VERSION = 1;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private PublicOpenSpaceDBHelper dbHelper;

    static final String TABLE_NAME = "tb_pos_publicopenspace";
    static final String COLUMN_ID = "pos_id";
    static final String COLUMN_ADDRESS = "pos_address";

    private String[] allColumns = {
            COLUMN_ID,
            COLUMN_ADDRESS
    };

}
