package com.learning.firhan.aquacare.Helpers;

import android.database.Cursor;

import com.learning.firhan.aquacare.Models.FishModel;

public class FishHelper {
    public FishModel convertCursorToModel(Cursor cursor){
        FishModel fishModel = new FishModel(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7)
        );

        return fishModel;
    }
}
