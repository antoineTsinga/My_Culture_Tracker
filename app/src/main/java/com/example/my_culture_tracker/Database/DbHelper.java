package com.example.my_culture_tracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.my_culture_tracker.Model.ListItem;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "mytable";
    private static final String DATABASE_NAME = "qrcode.db";

    private static final String COL_1 = "id";
    private static final String COL_2 = "code";
    private static final String COL_3 = "type";



    public DbHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+"(id INTEGER PRIMARY KEY AUTOINCREMENT, " + "code TEXT UNIQUE, type TEXT)");
    }

    @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Insert raw in SQLite database
    public boolean insertData(String code, String type){

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,code);
        contentValues.put(COL_3,type);

        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(TABLE_NAME,null,contentValues);

        return result != -1;
    }

    // Retrieve all informations about List items in SQLite database
    public ArrayList<ListItem> getAllInformations(){
        ArrayList<ListItem> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * FROM " + TABLE_NAME,null);

        if(cursor != null){
            while (cursor.moveToNext()){
                int id = cursor.getInt(0);
                String code = cursor.getString(1);
                String type = cursor.getString(2);

                ListItem listItem = new ListItem(id,code,type);
                arrayList.add(listItem);
            }
            cursor.close();
        }


        return arrayList;

    }

    public boolean notExist(String code){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_2 + "='"+code+"'",null);

        if(cursor != null){
            boolean count = cursor.moveToNext();
            cursor.close();
            return !count;
        }else{
            return false;
        }



    }

    public void deleteRow(int value){
        System.out.println("---------------- deleting....");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE " + COL_1 + "='"+value+"'");
    }
}
