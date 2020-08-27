package id.thony.android.quranlite.data.source.preferences;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

public class BookmarkPreferencesSource {

    private static final String BOOKMARK_PREFERENCE_KEY = "BOOKMARK_PREFERENCE_KEY";
    private final SharedPreferences sharedPreferences;

    public BookmarkPreferencesSource(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @SuppressLint("ApplySharedPref")
    public void putValue(String preference) {
        this.sharedPreferences.edit()
                .putString(BOOKMARK_PREFERENCE_KEY, preference)
                .commit();
    }

    public String getValue() {
        return this.sharedPreferences.getString(BOOKMARK_PREFERENCE_KEY, "");
    }
}
