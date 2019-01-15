package id.fathonyfath.quranreader.tasks;

public interface OnTaskListener<T> {
    void onProgress(float progress);
    void onFinished(T result);
}