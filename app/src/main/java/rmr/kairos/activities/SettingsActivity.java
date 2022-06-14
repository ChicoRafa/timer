package rmr.kairos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import rmr.kairos.R;

public class SettingsActivity extends AppCompatActivity {
    private final int RQ_MAIN = 10;
    private final String IK_MAIN = "main_key";
    private Intent intentFromMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.intentFromMain = getIntent();

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        public int workValue;

        public Integer getWorkValue() {
            return Integer.valueOf(workValue);
        }

        public void setWorkValue(Integer workValue) {
            this.workValue = workValue;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SeekBarPreference sbWork = findPreference("work_bar_key");
            SeekBarPreference sbBreak = findPreference("break_bar_key");
            SeekBarPreference sbSleep = findPreference("sleep_bar_key");
            if (sbWork != null) {
                sbWork.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                        if (newValue instanceof Integer) {
                            Integer workValueInt;
                            try {
                                workValueInt = (Integer) newValue;
                                setWorkValue(workValueInt);
                                // Do something with the value
                            } catch (NumberFormatException nfe) {
                                Toast.makeText(getActivity(),
                                        "SeekBarPreference is a Integer, but it caused a NumberFormatException",
                                        Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            return true;
                        } else {
                            String objType = newValue.getClass().getName();
                            Toast.makeText(getActivity(),
                                    "SeekBarPreference is not a Integer, it is " + objType,
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain = new Intent(SettingsActivity.this, MainActivity.class);
        intentToMain.putExtra(IK_MAIN, RQ_MAIN);
        setResult(RQ_MAIN, intentToMain);
        finish();
    }
}