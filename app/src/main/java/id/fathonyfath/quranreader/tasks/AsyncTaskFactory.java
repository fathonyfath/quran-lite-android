package id.fathonyfath.quranreader.tasks;

public interface AsyncTaskFactory<T extends BaseAsyncTask> {

    T create();
}
