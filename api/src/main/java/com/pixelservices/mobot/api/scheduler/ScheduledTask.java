package com.pixelservices.mobot.api.scheduler;

import com.pixelservices.mobot.api.modules.MbModule;
import org.jetbrains.annotations.Nullable;

public class ScheduledTask {

    private final int taskId;
    private final MbModule module;
    private final Runnable runnable;
    private final long nextRun;
    private final long period;
    private final boolean async;

    private boolean cancelled;

    public ScheduledTask(int taskId, MbModule module, Runnable runnable, long nextRun, long period, boolean async) {
        this.taskId = taskId;
        this.module = module;
        this.runnable = runnable;
        this.nextRun = nextRun;
        this.period = period;
        this.async = async;
    }

    /**
     * Gets the task ID.
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * Gets the module that ran this task.
     */
    public MbModule getModule() {
        return module;
    }

    /**
     * Gets the runnable associated with this task.
     */
    public Runnable getRunnable() {
        return runnable;
    }

    /**
     * Returns whether this task is asynchronous.
     */
    public boolean isAsync() {
        return async;
    }

    /**
     * Returns whether this task is repeating.
     */
    public boolean isRepeating() {
        return period > 0;
    }

    /**
     * Returns whether this task is cancelled.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancels this task.
     */
    public void cancel() {
        this.cancelled = true;
    }

    /**
     * Determines if this task should run at the given tick.
     */
    public boolean shouldRunAtTick(long currentTick) {
        if (currentTick < nextRun) {
            return false;
        }

        if (!isRepeating()) {
            return true;
        }

        long ticksSinceScheduled = currentTick - nextRun;
        return ticksSinceScheduled % period == 0;
    }

}
