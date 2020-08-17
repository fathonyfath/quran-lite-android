package id.fathonyfath.quran.lite.useCase;

import android.content.Context;
import android.content.res.Configuration;

import id.fathonyfath.quran.lite.data.ConfigRepository;
import id.fathonyfath.quran.lite.models.config.DayNightPreference;
import id.fathonyfath.quran.lite.utils.scheduler.Schedulers;

public class PutDayNightPreferenceUseCase extends BaseUseCase {

    private final Context context;
    private final ConfigRepository configRepository;

    private DayNightPreference updateWith;
    private UseCaseCallback<Boolean> callback;

    public PutDayNightPreferenceUseCase(Context context, ConfigRepository configRepository) {
        this.context = context;
        this.configRepository = configRepository;
    }

    public void setArguments(DayNightPreference updateWith) {
        this.updateWith = updateWith;
    }

    public void setCallback(UseCaseCallback<Boolean> callback) {
        this.callback = callback;
    }

    @Override
    protected void task() {
        if (this.updateWith == null) {
            postErrorToMainThread(new IllegalStateException("updateWith params should not be null."));
            return;
        }

        Schedulers.IO().execute(new Runnable() {
            @Override
            public void run() {
                DayNightPreference current = configRepository.getDayNightPreference();
                configRepository.putDayNightPreference(updateWith);
                comparePreviousWithUpdateWith(current, updateWith);
            }
        });
    }

    private void comparePreviousWithUpdateWith(final DayNightPreference previous, final DayNightPreference updateWith) {
        Schedulers.Computation().execute(new Runnable() {
            @Override
            public void run() {
                // Summary of this branching is that if we receive same preference we will return false
                // If we receive previous with SYSTEM preference, we will check whether system DayNight
                // will be the same as updateWith, do the opposite on updateWith with SYSTEM preference
                if (previous == updateWith) {
                    postResultToMainThread(false);
                } else if (previous == DayNightPreference.SYSTEM) {
                    SystemDayNight currentDayNight = getSystemUiDayNightStatus();
                    if (updateWith == DayNightPreference.DAY && currentDayNight == SystemDayNight.DAY) {
                        postResultToMainThread(false);
                    } else if (updateWith == DayNightPreference.NIGHT && currentDayNight == SystemDayNight.NIGHT) {
                        postResultToMainThread(false);
                    } else {
                        postResultToMainThread(true);
                    }
                } else if (updateWith == DayNightPreference.SYSTEM) {
                    SystemDayNight willResolveInto = getSystemUiDayNightStatus();
                    if (willResolveInto == SystemDayNight.DAY && previous == DayNightPreference.DAY) {
                        postResultToMainThread(false);
                    } else if (willResolveInto == SystemDayNight.NIGHT && previous == DayNightPreference.NIGHT) {
                        postResultToMainThread(false);
                    } else {
                        postResultToMainThread(true);
                    }
                } else {
                    postResultToMainThread(true);
                }
            }
        });
    }

    private void postResultToMainThread(final boolean isUpdateDifferentFromPrevious) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResult(isUpdateDifferentFromPrevious);
                }
            }
        });
    }

    private void postErrorToMainThread(final Exception e) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
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

    private enum SystemDayNight {
        DAY, NIGHT
    }

    public static class Factory implements UseCaseFactory<PutDayNightPreferenceUseCase> {

        private final Context context;
        private final ConfigRepository configRepository;

        public Factory(Context context, ConfigRepository configRepository) {
            this.context = context;
            this.configRepository = configRepository;
        }

        @Override
        public PutDayNightPreferenceUseCase create() {
            return new PutDayNightPreferenceUseCase(this.context, this.configRepository);
        }
    }
}
