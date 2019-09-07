package id.fathonyfath.quranlite.utils.scheduler;

public final class Schedulers {

    private static Scheduler main;
    private static Scheduler io;
    private static Scheduler computation;

    private Schedulers() {
    }

    public static Scheduler Main() {
        if (main == null) {
            main = new MainScheduler();
        }

        return main;
    }

    public static Scheduler IO() {
        if (io == null) {
            io = new IOScheduler();
        }

        return io;
    }

    public static Scheduler Computation() {
        if (computation == null) {
            computation = new ComputationScheduler();
        }

        return computation;
    }
}
