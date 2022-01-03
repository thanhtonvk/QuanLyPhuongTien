package com.example.quanlyphuongtien.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(@Nullable Context context) {
        super(context, "remember.sqlite", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE REMEMBER(" +
                "ID INT PRIMARY KEY," +
                "USERNAME NTEXT," +
                "PASSWORD NTEXT," +
                "[CHECK] INT," +
                "REMEMBER INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Remember GetRemember() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM REMEMBER", null);
        cursor.moveToFirst();
        Remember remember = null;
        if (cursor.getCount() >0) {
            remember = new Remember(cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
        }
        database.close();
        cursor.close();
        return remember;
    }

    public void Delete() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete("REMEMBER", "ID = ?", new String[]{"1"});
        database.close();
    }

    public void Add(Remember remember) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ID", 1);
        values.put("USERNAME", remember.getUsername());
        values.put("PASSWORD", remember.getPassword());
        values.put("[CHECK]", remember.getCheck());
        values.put("REMEMBER", remember.getRemember());
        database.insert("REMEMBER", null, values);
        database.close();
    }


}
