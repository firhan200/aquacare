package com.learning.firhan.aquacare.Helpers;

import android.database.Cursor;

import com.learning.firhan.aquacare.Constants.DateTypes;
import com.learning.firhan.aquacare.Models.FishModel;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public String getAgeInDays(String purchaseDate){
        String daysLabel = "0d";

        //format purchaseDate to Date Format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTypes.FISH_PURCHASE_DATE_TYPE);
        try {
            Date purchaseDateFormat = simpleDateFormat.parse(purchaseDate);

            DateTime startDate = new DateTime(purchaseDateFormat);
            DateTime endDate = new DateTime();

            Days d = Days.daysBetween(startDate, endDate);
            int days = d.getDays();

            daysLabel = String.valueOf(days)+"d";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return daysLabel;
    }
}
