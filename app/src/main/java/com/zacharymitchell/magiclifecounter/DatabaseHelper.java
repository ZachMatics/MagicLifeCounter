package com.zacharymitchell.magiclifecounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "score_history5.db";
    public static final String TABLE_NAME = "scores";
    public static final String COL_1 = "_id";
    public static final String COL_2 = "TOP_SCORE";
    public static final String COL_3 = "TOP_POISON_COUNT";
    public static final String COL_4 = "BOTTOM_SCORE";
    public static final String COL_5 = "BOTTOM_POISON_COUNT";
    public static final String COL_6 = "CURRENT_TURN_NAME";
    public static final String COL_7 = "TURN_NUMBER";
    public static final String COL_8 = "TURN_TIME";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " TOP_SCORE INTEGER, TOP_POISON_COUNT INTEGER, BOTTOM_SCORE INTEGER, " +
                "BOTTOM_POISON_COUNT, CURRENT_TURN_NAME STRING, TURN_NUMBER INTEGER, TURN_TIME STRING)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String topScore, String topPoisonCount, String bottomScore, String bottomPoisonCount,
                              String currentTurnName, String turnNumber, String currentTurnTimeInterval) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, topScore);
        contentValues.put(COL_3, topPoisonCount);
        contentValues.put(COL_4, bottomScore);
        contentValues.put(COL_5, bottomPoisonCount);
        contentValues.put(COL_6, currentTurnName);
        contentValues.put(COL_7, turnNumber);
        contentValues.put(COL_8, currentTurnTimeInterval);

        long result = db.insert(TABLE_NAME, null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME + " ORDER BY "+ COL_1 +" DESC", null);
        return result;

    }
    public Cursor lastRow() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME + " ORDER BY "+ COL_1 +" DESC LIMIT 1", null);
        return result;
    }

    //    public void deleteDatabase() {
//        this.deleteDatabase("score_history.db");
//    }
//public void deleteDatabase (DatabaseHelper myDb) throws SQLException {
//    myDb = DatabaseHelper.getWritableDatabase ();
//    myDb.execSQL ("drop table "+TABLE_NAME);
//    myDb.close ();
//    this.DatabaseHelper.onCreate (this.myDb);
//}
    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.execSQL("delete from "+ TABLE_NAME);

        db.close();
    }



}