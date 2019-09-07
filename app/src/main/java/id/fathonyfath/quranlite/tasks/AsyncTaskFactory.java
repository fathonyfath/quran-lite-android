package id.fathonyfath.quranlite.tasks;

public interface AsyncTaskFactory<T extends BaseAsyncTask> {

    T create();
}
