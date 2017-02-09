package org.davidcampelo.post.model;

import android.content.Context;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Generic DAO class to be used by the SQL import, which is going to call
 * the method exec() only
 *
 */
public class GenericDAO extends DAO {

    public GenericDAO(Context context) {
        super(context);
    }

    @Override
    protected String getTableName() {
        return null;
    }

    @Override
    protected String[] getTableColumns() {
        return new String[0];
    }

    @Override
    protected String getTableCreateCommand() {
        return null;
    }

    /**
     * The only method called!
     */
    public void exec(String sql) {
        super.exec(sql);
    }

    @Override
    public void resetData() {
        //
    }

    @Override
    public void dump(FileWriter out) throws IOException {
        //
    }
}
