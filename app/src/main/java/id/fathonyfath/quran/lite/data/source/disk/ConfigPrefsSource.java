package id.fathonyfath.quran.lite.data.source.disk;

import android.content.SharedPreferences;

class ConfigPrefsSource {

    private static final String DARK_NIGHT_SETTING = "DarkNightSetting";

    private final SharedPreferences preferences;

    ConfigPrefsSource(SharedPreferences preferences) {
        this.preferences = preferences;
    }


}
