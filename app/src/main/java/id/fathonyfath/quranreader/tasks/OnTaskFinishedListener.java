package id.fathonyfath.quranreader.tasks;

public interface OnTaskFinishedListener<T> {
    void onFinished(T result);
}