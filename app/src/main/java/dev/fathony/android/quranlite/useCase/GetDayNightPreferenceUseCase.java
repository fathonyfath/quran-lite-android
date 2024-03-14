package dev.fathony.android.quranlite.useCase;

import dev.fathony.android.quranlite.data.ConfigRepository;
import dev.fathony.android.quranlite.models.config.DayNightPreference;
import dev.fathony.android.quranlite.utils.scheduler.Schedulers;

public class GetDayNightPreferenceUseCase extends BaseUseCase {

    private final ConfigRepository configRepository;

    private UseCaseCallback<DayNightPreference> callback;

    public GetDayNightPreferenceUseCase(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    protected void task() {
        Schedulers.IO().execute(new Runnable() {
            @Override
            public void run() {
                final DayNightPreference preference = configRepository.getDayNightPreference();
                postResultToMainThread(preference);
            }
        });
    }

    public void postResultToMainThread(final DayNightPreference preference) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResult(preference);
                }
            }
        });
    }

    public void setCallback(UseCaseCallback<DayNightPreference> callback) {
        this.callback = callback;
    }

    public static class Factory implements UseCaseFactory<GetDayNightPreferenceUseCase> {

        private final ConfigRepository configRepository;

        public Factory(ConfigRepository configRepository) {
            this.configRepository = configRepository;
        }

        @Override
        public GetDayNightPreferenceUseCase create() {
            return new GetDayNightPreferenceUseCase(this.configRepository);
        }
    }
}
