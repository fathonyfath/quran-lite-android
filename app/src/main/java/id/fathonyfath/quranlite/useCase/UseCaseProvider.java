package id.fathonyfath.quranlite.useCase;

import java.util.HashMap;
import java.util.Map;

public class UseCaseProvider {

    private static Map<Class<? extends BaseUseCase>, UseCaseFactory> useCaseFactoryHolder = new HashMap<>();
    private static Map<Class<? extends BaseUseCase>, BaseUseCase> useCaseHolder = new HashMap<>();

    public static <T extends BaseUseCase> void registerFactory(Class<T> asyncTaskClass, UseCaseFactory<T> factory) {
        if (!UseCaseProvider.useCaseFactoryHolder.containsKey(asyncTaskClass)) {
            UseCaseProvider.useCaseFactoryHolder.put(asyncTaskClass, factory);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseUseCase> T createUseCase(Class<T> useCaseClass) {
        clearUseCase(useCaseClass);

        UseCaseFactory<T> factory = UseCaseProvider.useCaseFactoryHolder.get(useCaseClass);
        if (factory == null) {
            throw new IllegalStateException("No factory registered for class " + useCaseClass.getCanonicalName());
        }

        T useCase = factory.create();
        UseCaseProvider.useCaseHolder.put(useCaseClass, useCase);
        return useCase;
    }


    @SuppressWarnings("unchecked")
    public static <T extends BaseUseCase> T getUseCase(Class<T> useCaseClass) {
        BaseUseCase cached = UseCaseProvider.useCaseHolder.get(useCaseClass);
        if (cached != null) {
            return (T) cached;
        } else {
            return null;
        }
    }

    public static <T extends BaseUseCase> void clearUseCase(Class<T> useCaseClass) {
        BaseUseCase cached = UseCaseProvider.useCaseHolder.get(useCaseClass);
        if (cached != null) {
            cached.cancel();
            UseCaseProvider.useCaseHolder.remove(useCaseClass);
        }
    }

    public static void clearAllUseCase() {
        for (Map.Entry<Class<? extends BaseUseCase>, BaseUseCase> entry : useCaseHolder.entrySet()) {
            entry.getValue().cancel();
        }
        UseCaseProvider.useCaseHolder.clear();
    }
}
