package org.davidcampelo.post.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Question` Database Accesss Object
 *
 * @author David Campelo <david.campelo@gmail.com>
 */
public class QuestionDAO extends DAO {


    static final String TABLE_NAME = "tb_qst_question";
    static final String COLUMN_ID = "qst_id";
    static final String COLUMN_TITLE = "qst_title";
    static final String COLUMN_TYPE = "qst_type";

    static String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_TYPE
    };

    static final String TABLE_CREATE_CMD = "CREATE TABLE "+ TABLE_NAME +" ( "
            + COLUMN_ID +" INTEGER primary key autoincrement, "
            + COLUMN_TITLE +" TEXT not null, "
            + COLUMN_TYPE +" TEXT not null);";

    public QuestionDAO(Context context) {
        super(context);
    }

    public Question insert(Question object){

        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, object.title);
        values.put(COLUMN_TITLE, object.type.name());

        object.id = insert(TABLE_NAME, values);

        return object;
    }

    public boolean update(Question object){

        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, object.title);
        values.put(COLUMN_TITLE, object.type.name());

        return (update(TABLE_NAME, values, COLUMN_ID +"="+ object.id) > 0);
    }

    public Question get(long id) {
        Question object = new Question(); // if no object was found, just return an empty object

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, COLUMN_ID +"="+ id);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            object = cursorToObject(cursor);
        }

        cursor.close();

        return object;
    }

    public ArrayList<Question> getAll() {
        ArrayList<Question> list = new ArrayList<Question>();

        Cursor cursor = select(TABLE_NAME, TABLE_COLUMNS, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            list.add(cursorToObject(cursor));
        }

        cursor.close();

        return list;
    }

    public long delete(Question object){
        return delete(TABLE_NAME, COLUMN_ID +"="+ object.id);
    }

    public static Question staticGet(Context ctx, long id) {
        Question object;
        QuestionDAO dbAdapter = new QuestionDAO(ctx);
        dbAdapter.open();
        object = dbAdapter.get(id);
        dbAdapter.close();
        return object;
    }

    public static Question staticInsert(Context ctx, Question object) {
        QuestionDAO dbAdapter = new QuestionDAO(ctx);
        dbAdapter.open();
        object = dbAdapter.insert(object);
        dbAdapter.close();

        return object;
    }
    public static void staticUpdate(Context ctx, Question object) {
        QuestionDAO dbAdapter = new QuestionDAO(ctx);
        dbAdapter.open();
        dbAdapter.update(object);
        dbAdapter.close();
    }
    public static void staticDelete(Context ctx, Question object) {
        QuestionDAO dbAdapter = new QuestionDAO(ctx);
        dbAdapter.open();
        dbAdapter.delete(object);
        dbAdapter.close();
    }

    private Question cursorToObject(Cursor cursor) {
        return new Question(
                cursor.getLong(0),      // id
                cursor.getString(1),    // title
                Question.QuestionType.valueOf(cursor.getString(2)), // type
                null                    // TODO: retrieve options
        );
    }
}
