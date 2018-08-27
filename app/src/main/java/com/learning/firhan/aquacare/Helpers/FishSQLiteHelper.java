package com.learning.firhan.aquacare.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.learning.firhan.aquacare.Models.AquariumModel;
import com.learning.firhan.aquacare.Models.FishModel;

public class FishSQLiteHelper extends SQLiteOpenHelper {
    SQLiteDatabase sqLiteDatabase;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Fish.db";


    public static final String TABLE_NAME = "fish";
    public static final String COL_ID = "id";
    public static final String COL_AQUARIUM_ID = "aquarium_id";
    public static final String COL_IMAGE_URI = "image_uri";
    public static final String COL_IMAGE_THUMBNAIL_URI = "image_thumbnail_uri";
    public static final String COL_NAME = "name";
    public static final String COL_TYPE = "type";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_PURCHASE_DATE = "purchase_date";

    public FishSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+TABLE_NAME +
                "("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ""+COL_AQUARIUM_ID+" INTEGER," +
                ""+COL_IMAGE_URI+" VARCHAR," +
                ""+COL_IMAGE_THUMBNAIL_URI+" VARCHAR," +
                ""+COL_NAME+" VARCHAR," +
                ""+COL_TYPE+" VARCHAR," +
                ""+COL_DESCRIPTION+" VARCHAR," +
                ""+COL_PURCHASE_DATE+" VARCHAR"+
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public long insertData(FishModel model){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_AQUARIUM_ID, model.getAquariumIdId());
        contentValues.put(COL_IMAGE_URI, model.getImageUri());
        contentValues.put(COL_IMAGE_THUMBNAIL_URI, model.getImageUri());
        contentValues.put(COL_NAME, model.getName());
        contentValues.put(COL_TYPE, model.getType());
        contentValues.put(COL_DESCRIPTION, model.getDescription());
        contentValues.put(COL_PURCHASE_DATE, model.getPurchaseDate());
        long newRowId = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return newRowId;
    }

    public int deleteData(int id){
        int deletedRowsId = sqLiteDatabase.delete(TABLE_NAME, COL_ID+"="+id, null);
        return deletedRowsId;
    }

    public Cursor getAllDataByAquariumId(int aquariumId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COL_AQUARIUM_ID+"="+aquariumId+" ORDER BY Id DESC", null);
        return result;
    }
}
