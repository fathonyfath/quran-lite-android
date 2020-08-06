package id.fathonyfath.quran.lite.data.source.preferences;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

public class DayNightPreferencesSource {

    private static final String DAY_NIGHT_PREFERENCE_KEY = "DAY_NIGHT_PREFERENCE_KEY";
    private final SharedPreferences sharedPreferences;

    public DayNightPreferencesSource(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @SuppressLint("ApplySharedPref")
    public void putValue(String preference) {
        this.sharedPreferences.edit()
                .putString(DAY_NIGHT_PREFERENCE_KEY, preference)
                .commit();
    }

    public String getValue() {
        return this.sharedPreferences.getString(DAY_NIGHT_PREFERENCE_KEY, "");
    }
}
