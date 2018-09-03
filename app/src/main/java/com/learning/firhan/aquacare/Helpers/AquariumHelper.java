package com.learning.firhan.aquacare.Helpers;

import android.database.Cursor;

import com.learning.firhan.aquacare.Models.AquariumModel;
import com.learning.firhan.aquacare.Models.FishModel;

public class AquariumHelper {
    public AquariumModel convertCursorToModel(Cursor cursor){
        AquariumModel aquariumModel = new AquariumModel(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getDouble(5),
                cursor.getDouble(6),
                cursor.getDouble(7)
        );

        return aquariumModel;
    }
}
