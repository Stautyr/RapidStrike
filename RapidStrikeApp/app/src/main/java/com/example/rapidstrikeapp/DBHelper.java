/*
 *
 * CREDIT FOR PART OF PROGRAM
 * WEBSITE USED:
 * https://www.w3schools.blog/android-sqlite-example-with-spinner
 *
 *
 * */
package com.example.rapidstrikeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "teamSelectionSpinner";
    private static final String TABLE_NAME = "teams";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int l)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    //Insert Team names into database
    public void insertTeam(String team)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, team);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<String> getAllTeams()
    {
        List<String> list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}
