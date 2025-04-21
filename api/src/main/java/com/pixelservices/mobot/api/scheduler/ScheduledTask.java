package com.pixelservices.mobot.api.scheduler;

import com.pixelservices.mobot.api.modules.MbModule;

import java.util.concurrent.ScheduledFuture;

public class ScheduledTask {

    private final int taskId;
    private final MbModule module;
    private final Runnable runnable;
    private final boolean async;
    private final boolean repeating;

    private ScheduledFuture<?> future;

    private boolean cancelled;

    public ScheduledTask(int taskId, MbModule module, Runnable runnable, boolean async, boolean repeating) {
        this.taskId = taskId;
        this.module = module;
        this.runnable = runnable;
        this.async = async;
        this.repeating = repeating;
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

    public boolean isRepeating() {
        return repeating;
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

        if (future != null) {
            future.cancel(false);
        }
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

}
