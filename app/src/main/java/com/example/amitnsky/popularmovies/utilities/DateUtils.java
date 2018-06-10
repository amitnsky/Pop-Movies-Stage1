package com.example.amitnsky.popularmovies.utilities;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    //parse year fromm a date string of format 2018-10-22, returns year, 2018
    public static String getYear(String release_date) {
        if (release_date==null){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        String year = "N/A";
        try {
            calendar.setTime((new SimpleDateFormat("yyyy-MM-dd")).parse(release_date));
            year = Integer.toString(calendar.get(Calendar.YEAR));
        } catch (ParseException e) {
            Log.e("DateUtils", "Failed to parse release_date from : " + release_date, e);
        }
        return year;
    }

    //format runtime as 2hr 30min
    public static String getFormattedRuntime(int runtime) {
        int hr = runtime / 60;
        int min = runtime % 60;
        if (hr <= 0) {
            return String.valueOf(runtime) + "min";
        } else {
            return String.valueOf(hr) + "hr " + String.valueOf(min) + "min";
        }
    }

    public static float getRating(double rate){
        return Float.parseFloat(String.valueOf(rate));
    }
}
