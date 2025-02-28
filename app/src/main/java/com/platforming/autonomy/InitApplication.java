package com.platforming.autonomy;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

public class InitApplication extends Application {

    private int appliedTheme;
    private int appliedFont;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("timeline", "initApplication");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getBoolean("firstActivate", true)){
            Calendar calendar_timer = Calendar.getInstance();
            calendar_timer.set(Calendar.HOUR_OF_DAY, 8);
            calendar_timer.set(Calendar.MINUTE, 10);
            calendar_timer.set(Calendar.SECOND, 0);
            Calendar calendar_now = Calendar.getInstance();

            if(calendar_timer.before(calendar_now))
                calendar_timer.set(Calendar.DATE, calendar_now.get(Calendar.DATE) + 1);

            SharedPreferences.Editor editor = pref.edit();
            editor.putLong("time_selfDiagnosis", calendar_timer.getTimeInMillis());
            editor.putLong("time_schedule", calendar_timer.getTimeInMillis());
            editor.putBoolean("firstActivate", false);
            editor.apply();
        }
    }

    public int getAppliedTheme() {
        return appliedTheme;
    }

    public int getAppliedFont() {
        return appliedFont;
    }

    public void setAppliedTheme(int appliedTheme) {
        this.appliedTheme = appliedTheme;
    }

    public void refreshAppliedTheme(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        appliedTheme = pref.getInt("theme", 0);
    }

    public void refreshAppliedFont(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        appliedFont = pref.getInt("font", 0);
    }

    public void setAppliedFont(int appliedFont) {
        this.appliedFont = appliedFont;
    }
}