package dev.fathony.android.quranlite.data;

import dev.fathony.android.quranlite.data.source.preferences.DayNightPreferencesSource;
import dev.fathony.android.quranlite.models.config.DayNightPreference;
import dev.fathony.android.quranlite.utils.EnumUtil;

public class ConfigRepository {

    private final DayNightPreferencesSource dayNightPreferencesSource;

    public ConfigRepository(DayNightPreferencesSource dayNightPreferencesSource) {
        this.dayNightPreferencesSource = dayNightPreferencesSource;
    }

    public void putDayNightPreference(DayNightPreference preference) {
        this.dayNightPreferencesSource.putValue(preference.toString());
    }

    public DayNightPreference getDayNightPreference() {
        return EnumUtil.safeValueOf(this.dayNightPreferencesSource.getValue(), DayNightPreference.SYSTEM);
    }
}
