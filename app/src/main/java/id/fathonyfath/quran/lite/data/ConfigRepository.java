package id.fathonyfath.quran.lite.data;

import id.fathonyfath.quran.lite.data.source.preferences.DayNightPreferencesSource;
import id.fathonyfath.quran.lite.models.config.DayNightPreference;
import id.fathonyfath.quran.lite.utils.EnumUtil;

public class ConfigRepository {

    private final DayNightPreferencesSource dayNightPreferencesSource;

    ConfigRepository(DayNightPreferencesSource dayNightPreferencesSource) {
        this.dayNightPreferencesSource = dayNightPreferencesSource;
    }

    public void putDayNightPreference(DayNightPreference preference) {
        this.dayNightPreferencesSource.putValue(preference.toString());
    }

    public DayNightPreference getDayNightPreference() {
        return EnumUtil.safeValueOf(this.dayNightPreferencesSource.getValue(), DayNightPreference.SYSTEM);
    }
}
