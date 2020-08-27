package id.thony.android.quranlite.useCase;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseUseCase {

    private AtomicBoolean executed = new AtomicBoolean(false);
    private boolean cancelled = false;

    public final void run() {
        if (this.executed.compareAndSet(false, true)) {
            this.task();
        } else {
            throw new IllegalStateException("UseCase only can be run once.");
        }
    }

    protected abstract void task();

    public final void cancel() {
        if (this.cancelled) {
            return;
        }

        setCancelled(true);
    }

    public final boolean isExecuted() {
        return executed.get();
    }

    protected boolean isCancelled() {
        return this.cancelled;
    }

    protected void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
