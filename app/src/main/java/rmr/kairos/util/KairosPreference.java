package rmr.kairos.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import rmr.kairos.activities.MainActivity;
import rmr.kairos.activities.SettingsActivity;

public class KairosPreference {
    private static final String WORK_VALUE= "work_bar_key";
    public Integer getWorkValue(Context context){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        return preference.getInt(WORK_VALUE,25);
    }




}