package id.thony.android.quranlite.useCase;

import android.content.Context;
import android.content.res.Configuration;

import id.thony.android.quranlite.data.ConfigRepository;
import id.thony.android.quranlite.models.DayNight;
import id.thony.android.quranlite.models.config.DayNightPreference;

public class GetDayNightUseCase extends BaseUseCase {

    private final Context context;
    private final ConfigRepository configRepository;

    private UseCaseCallback<DayNight> callback;

    public GetDayNightUseCase(Context context, ConfigRepository configRepository) {
        this.context = context;
        this.configRepository = configRepository;
    }

    @Override
    protected void task() {
        fetchDayNightPreference();
    }

    public void setCallback(UseCaseCallback<DayNight> callback) {
        this.callback = callback;
    }

    private void fetchDayNightPreference() {
        final DayNightPreference preference = this.configRepository.getDayNightPreference();
        processDayNightPreference(preference);
    }

    private void processDayNightPreference(DayNightPreference dayNightPreference) {
        DayNight dayNight = null;
        switch (dayNightPreference) {
            case SYSTEM:
                final SystemDayNight systemDayNight = getSystemUiDayNightStatus();
                dayNight = processSystemDayNight(systemDayNight);
                break;
            case DAY:
                dayNight = DayNight.DAY;
                break;
            case NIGHT:
                dayNight = DayNight.NIGHT;
                break;
        }

        final DayNight finalDayNight = dayNight;

        postResult(finalDayNight);
    }

    private SystemDayNight getSystemUiDayNightStatus() {
        int nightModeFlags = this.context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                // Here we set fallback to Day theme on system
                return SystemDayNight.DAY;
            case Configuration.UI_MODE_NIGHT_YES:
                return SystemDayNight.NIGHT;
        }

        throw new IllegalStateException("This flow is impossible to get.");
    }

    private DayNight processSystemDayNight(SystemDayNight systemDayNight) {
        switch (systemDayNight) {
            case DAY:
                return DayNight.DAY;
            case NIGHT:
                return DayNight.NIGHT;
        }

        throw new IllegalStateException("This flow is impossible to get.");
    }

    private void postResult(DayNight dayNight) {
        if (callback != null) {
            callback.onResult(dayNight);
        }
    }

    private enum SystemDayNight {
        DAY, NIGHT
    }

    public static class Factory implements UseCaseFactory<GetDayNightUseCase> {

        private final Context context;
        private final ConfigRepository configRepository;

        public Factory(Context context, ConfigRepository configRepository) {
            this.context = context;
            this.configRepository = configRepository;
        }

        @Override
        public GetDayNightUseCase create() {
            return new GetDayNightUseCase(this.context, this.configRepository);
        }
    }
}
