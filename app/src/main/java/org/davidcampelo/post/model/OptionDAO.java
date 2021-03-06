package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.davidcampelo.post.utils.Constants;
import org.davidcampelo.post.utils.StringUtils;

import java.util.ArrayList;

/**
 * Option Database Access Object
 *
 * @author David Campelo <david.campelo@gmail.com>
 */
public class OptionDAO extends DAO {


    static final String TABLE_NAME = "tb_opt_option";
    static final String COLUMN_ID = "opt_id";
    static final String COLUMN_ALIAS = "opt_alias";
    static final String COLUMN_VALUE = "opt_value";
    static final String COLUMN_TITLE = "opt_title";
    // Question this option belongs to
    static final String COLUMN_QST_NUMBER = "qst_number";
    // List of Question numbers which should be disabled in the UI if this Option is checked
    static final String DISABLED_QST_NUMBERS = "opt_disable_question_numbers";
    // Filled if this Option concerns to one and only POS
    static final String COLUMN_POS_ID = "pos_id";
    // PublicOpenSpace (happens for "Other" options)

    static String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_ALIAS,
            COLUMN_VALUE,
            COLUMN_TITLE,
            COLUMN_QST_NUMBER,
            DISABLED_QST_NUMBERS,
            COLUMN_POS_ID
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_ID +" INTEGER primary key autoincrement, "
            + COLUMN_ALIAS +" TEXT, "
            + COLUMN_VALUE +" TEXT, "
            + COLUMN_TITLE +" TEXT not null, "
            + COLUMN_QST_NUMBER +" TEXT, "
            + DISABLED_QST_NUMBERS +" TEXT, "
            + COLUMN_POS_ID +" INTEGER);";

    public OptionDAO(Context context) {
        super(context);
    }
    public  OptionDAO(Context context, DAOHelper dbHelper, SQLiteDatabase sqLiteDatabase) {
        super(context, dbHelper, sqLiteDatabase);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String[] getTableColumns() {
        return TABLE_COLUMNS;
    }

    @Override
    protected String getTableCreateCommand() {
        return TABLE_CREATE_CMD;
    }


    public Option insert(Option object){

        ContentValues values = new ContentValues();

        values.put(COLUMN_ALIAS, object.alias);
        values.put(COLUMN_VALUE, object.value);
        values.put(COLUMN_TITLE, object.title);
        values.put(COLUMN_QST_NUMBER, object.question.number);
        values.put(DISABLED_QST_NUMBERS,
                StringUtils.toString(object.disabledQuestionNumbers, Constants.DEFAULT_SEPARATOR));

        if (object.publicOpenSpace != null) {
            values.put(COLUMN_POS_ID, object.publicOpenSpace.id);
        }

        object.id = insert(TABLE_NAME, values);

        return object;
    }

    public boolean update(Option object){

        ContentValues values = new ContentValues();

        values.put(COLUMN_ALIAS, object.alias);
        values.put(COLUMN_VALUE, object.value);
        values.put(COLUMN_TITLE, object.title);
        values.put(COLUMN_QST_NUMBER, object.question.number);
        values.put(DISABLED_QST_NUMBERS,
                StringUtils.toString(object.disabledQuestionNumbers, Constants.DEFAULT_SEPARATOR));


        return (update(TABLE_NAME, values, COLUMN_ID +"="+ object.id) > 0);
    }

    /**
     * Retrieves all Option objects based on Question and PublicOpenSpace objects
     * @param question
     * @param publicOpenSpace if PublicOpenSpace.id == 0 then we should retrieve the "default"
     *                        options only, otherwise we will retrieve "default" options + custom
     *                        user Options previously inserted
     * @return
     */
    public ArrayList<Option> getAll(Question question, PublicOpenSpace publicOpenSpace) {
        ArrayList<Option> list = new ArrayList<Option>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_QST_NUMBER +" = '"+ question.number +"' AND "+
                " ("+ COLUMN_POS_ID+" IS NULL OR "+COLUMN_POS_ID+" = "+publicOpenSpace.id+" ) "+
                "ORDER BY "+ COLUMN_ID +" ASC");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.add(cursorToObject(cursor, question, publicOpenSpace));
        }

        cursor.close();

        return list;
    }

    /**
     * Retrieves all default options based on Question (the ones in the original POST itself)
     * @param question
     * @return
     */
    public ArrayList<Option> getAllDefaultByQuestion(Question question) {
        ArrayList<Option> list = new ArrayList<Option>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_QST_NUMBER +" = '"+ question.number +"' AND "+
                " ("+ COLUMN_POS_ID+" IS NULL OR "+COLUMN_POS_ID+" = 0 ) "+
                "ORDER BY "+ COLUMN_ID +" ASC");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Option option = cursorToObject(cursor, question, null);
            option.question = question;
            list.add(option);
        }

        cursor.close();

        return list;
    }

    public Option get(long id) {
        Option object = new Option(); // if no object was found, just return an empty object

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_ID +"="+ id);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            object = cursorToObject(cursor);
        }

        cursor.close();

        return object;
    }

    public ArrayList<Option> getAll() {
        ArrayList<Option> list = new ArrayList<Option>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.add(cursorToObject(cursor));
        }

        cursor.close();

        return list;
    }


    /**
     * Remove all custom added Option (using the "Other" option) related to a POS
     *
     * This method is called when a POS is being deleted (avoid leaks)
     */
    public boolean removeAllAddedByUser(PublicOpenSpace publicOpenSpace){
        return (delete(TABLE_NAME, COLUMN_POS_ID +"="+ publicOpenSpace.id) > 0);
    }

    /**
     * Remove all custom added Option (using the "Other" option).
     *
     * This method is called when a POS is being saved as all custom Options are being re-stored.
     */
    public boolean removeAllCustomAddedOptions(Question question, PublicOpenSpace publicOpenSpace){
        return (delete(TABLE_NAME, COLUMN_QST_NUMBER +"='"+ question.getNumber() +"' AND "+
                                   COLUMN_POS_ID +"="+ publicOpenSpace.id) > 0);
    }

    /**
     * If default Options are already stored in the DB
     */
    public boolean isPopulated() {
        return ( count() >= Constants.NUMBER_OF_OPTIONS );
    }
    private Option cursorToObject(Cursor cursor) {
        return cursorToObject(cursor, null, null);
    }

    private Option cursorToObject(Cursor cursor, Question question, PublicOpenSpace publicOpenSpace) {
        return new Option(
                cursor.getLong(0),      // id
                cursor.getString(1),    // alias
                cursor.getString(2),    // value
                cursor.getString(3),    // title
                question,               // Question
                StringUtils.toArrayList(cursor.getString(5), Constants.DEFAULT_SEPARATOR),    // disable_question_numbers
                publicOpenSpace         // PublicOpenSpace
        );
    }


}
