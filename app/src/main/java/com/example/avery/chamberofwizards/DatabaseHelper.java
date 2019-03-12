package com.example.avery.chamberofwizards;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class DatabaseHelper  extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "DownloadedBooks.db";
    public static final String TABLE_NAME = "downloaded_books_table";
    public static final String PRIMARY_KEY = "Book Key";
    public static final String COLUMN_1 = "Book Path";

    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, 1); // Creating the database

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME +  "(BOOK_KEY TEXT PRIMARY KEY, BOOK_PATH TEXT)"); //Excecutes a query
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); //Executes a query
        onCreate(sqLiteDatabase); //On Create will execute
    }

    public boolean insertData(String bookKey, String bookPath)
    {
        SQLiteDatabase db = this.getWritableDatabase(); //Instance of the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRIMARY_KEY, bookKey);
        contentValues.put(COLUMN_1, bookPath); //Inserting the values to the table.

        long result = db.insert(TABLE_NAME, null, contentValues); //Inserting NOTE: This returns a long value.
        //Kapag ang value nito ay -1, failed and insertion.

        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }


    }
}
