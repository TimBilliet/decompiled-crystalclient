package mapwriter.tasks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class Task implements Runnable {
    private Future<?> future = null;

    public abstract void onComplete();

    public abstract void run();

    public abstract boolean CheckForDuplicate();

    public final Future<?> getFuture() {
        return this.future;
    }

    public final void setFuture(Future<?> future) {
        this.future = future;
    }

    public final boolean isDone() {
        return (this.future != null && this.future.isDone());
    }

    public final void printException() {
        if (this.future != null)
            try {
                this.future.get();
            } catch (ExecutionException e) {
                Throwable rootException = e.getCause();
                rootException.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\tasks\Task.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */