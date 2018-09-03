package com.learning.firhan.aquacare.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.learning.firhan.aquacare.Models.AquariumModel;

public class AquariumSQLiteHelper extends SQLiteOpenHelper {
    SQLiteDatabase sqLiteDatabase;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Aquarium.db";


    public static final String TABLE_NAME = "aquarium";
    public static final String COL_ID = "id";
    public static final String COL_IMAGE_URI = "image_uri";
    public static final String COL_IMAGE_THUMBNAIL_URI = "image_thumbnail_uri";
    public static final String COL_NAME = "name";
    public static final String COL_DESCRIPTION= "description";
    public static final String COL_LENGTH = "length";
    public static final String COL_HEIGHT = "height";
    public static final String COL_WIDE = "wide";

    public AquariumSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+TABLE_NAME +
                "("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ""+COL_IMAGE_URI+" VARCHAR," +
                ""+COL_IMAGE_THUMBNAIL_URI+" VARCHAR," +
                ""+COL_NAME+" VARCHAR," +
                ""+COL_DESCRIPTION+" VARCHAR," +
                ""+COL_LENGTH+" REAL,"+
                ""+COL_HEIGHT+" REAL,"+
                ""+COL_WIDE+" REAL"+
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public long insertData(AquariumModel model){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_IMAGE_URI, model.getImageUri());
        contentValues.put(COL_IMAGE_THUMBNAIL_URI, model.getImageUri());
        contentValues.put(COL_NAME, model.getName());
        contentValues.put(COL_DESCRIPTION, model.getDescription());
        contentValues.put(COL_LENGTH, model.getAquariumLength());
        contentValues.put(COL_HEIGHT, model.getAquariumHeight());
        contentValues.put(COL_WIDE, model.getAquariumWide());
        long newRowId = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return newRowId;
    }

    public long updateData(AquariumModel model){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_IMAGE_URI, model.getImageUri());
        contentValues.put(COL_IMAGE_THUMBNAIL_URI, model.getImageUri());
        contentValues.put(COL_NAME, model.getName());
        contentValues.put(COL_DESCRIPTION, model.getDescription());
        contentValues.put(COL_LENGTH, model.getAquariumLength());
        contentValues.put(COL_HEIGHT, model.getAquariumHeight());
        contentValues.put(COL_WIDE, model.getAquariumWide());
        long newRowId = sqLiteDatabase.update(TABLE_NAME, contentValues, COL_ID+"="+model.getId(), null);
        return newRowId;
    }

    public int deleteData(int id){
        int deletedRowsId = sqLiteDatabase.delete(TABLE_NAME, COL_ID+"="+id, null);
        return deletedRowsId;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY Id DESC", null);
        //db.close();
        return result;
    }

    public AquariumModel getAquariumById(int aquariumId){
        AquariumHelper aquariumHelper = new AquariumHelper();

        //init model
        AquariumModel aquariumModel = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COL_ID+"="+aquariumId, null);

        if(result.getCount() > 0){
            //not null
            while(result.moveToNext()){
                aquariumModel = aquariumHelper.convertCursorToModel(result);
            }
        }

        //db.close();

        return aquariumModel;
    }
}
