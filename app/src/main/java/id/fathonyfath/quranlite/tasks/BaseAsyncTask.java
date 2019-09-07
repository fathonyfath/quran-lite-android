package id.fathonyfath.quranlite.tasks;

import android.os.AsyncTask;

public abstract class BaseAsyncTask<Params, Result> extends AsyncTask<Params, Float, Result> {

    private OnTaskListener<Result> onTaskListener;

    public void setOnTaskListener(OnTaskListener<Result> onTaskListener) {
        this.onTaskListener = onTaskListener;
    }

    protected void postResult(Result result) {
        if (this.onTaskListener != null) {
            this.onTaskListener.onFinished(result);
        }
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);

        if (this.onTaskListener != null) {
            this.onTaskListener.onProgress(values[0]);
        }
    }
}
