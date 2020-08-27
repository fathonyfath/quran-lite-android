package id.thony.android.quranlite.useCase;

public interface UseCaseCallback<T> {

    void onProgress(float progress);

    void onResult(T data);

    void onError(Throwable throwable);
}