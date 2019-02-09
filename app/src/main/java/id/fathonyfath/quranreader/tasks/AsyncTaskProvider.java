package id.fathonyfath.quranreader.tasks;

import java.util.HashMap;
import java.util.Map;

public class AsyncTaskProvider {

    private static Map<Class<? extends BaseAsyncTask>, AsyncTaskFactory> asyncTaskFactoryHolder = new HashMap<>();
    private static Map<Class<? extends BaseAsyncTask>, BaseAsyncTask> asyncTaskHolder = new HashMap<>();

    public static <T extends BaseAsyncTask> void registerFactory(Class<T> asyncTaskClass, AsyncTaskFactory<T> factory) {
        if (!AsyncTaskProvider.asyncTaskFactoryHolder.containsKey(asyncTaskClass)) {
            AsyncTaskProvider.asyncTaskFactoryHolder.put(asyncTaskClass, factory);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseAsyncTask> T getAsyncTask(Class<T> asyncTaskClass) {
        BaseAsyncTask cached = AsyncTaskProvider.asyncTaskHolder.get(asyncTaskClass);
        if (cached != null) {
            return (T) cached;
        }

        AsyncTaskFactory<T> factory = AsyncTaskProvider.asyncTaskFactoryHolder.get(asyncTaskClass);
        if (factory == null) {
            throw new IllegalStateException("No factory registered for class " + asyncTaskClass.getCanonicalName());
        }

        T asyncTask = factory.create();
        AsyncTaskProvider.asyncTaskHolder.put(asyncTaskClass, asyncTask);
        return asyncTask;
    }

    public static <T extends BaseAsyncTask> void clearAsyncTask(Class<T> asyncTaskClass) {
        BaseAsyncTask cached = AsyncTaskProvider.asyncTaskHolder.get(asyncTaskClass);
        if (cached != null) {
            cached.cancel(true);
            cached.setOnTaskListener(null);
            AsyncTaskProvider.asyncTaskHolder.remove(asyncTaskClass);
        }
    }

    public static void clearAllAsyncTask() {
        AsyncTaskProvider.asyncTaskHolder.clear();
    }
}
