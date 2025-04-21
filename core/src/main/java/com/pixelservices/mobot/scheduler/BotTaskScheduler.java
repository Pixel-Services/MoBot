package com.pixelservices.mobot.scheduler;

import com.pixelservices.logger.Logger;
import com.pixelservices.logger.LoggerFactory;
import com.pixelservices.mobot.api.modules.MbModule;
import com.pixelservices.mobot.api.scheduler.ScheduledTask;
import com.pixelservices.mobot.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BotTaskScheduler implements TaskScheduler {

    private final Logger logger;
    private final Map<Integer, ScheduledTask> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger taskIdCounter = new AtomicInteger(0);
    private final ScheduledExecutorService scheduler;
    private final ExecutorService asyncExecutor;

    public BotTaskScheduler() {
        this.logger = LoggerFactory.getLogger("Console");

        this.scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread thread = new Thread(r, "TaskScheduler-SchedulerThread");
            thread.setDaemon(true);
            return thread;
        });

        this.asyncExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(@NotNull Runnable runnable) {
                Thread thread = new Thread(runnable, "TaskScheduler-AsyncWorker-" + counter.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    @Override
    public Map<Integer, ScheduledTask> getTasks() {
        return Collections.unmodifiableMap(tasks);
    }

    @Override
    public ScheduledTask runTask(@NotNull MbModule module, Runnable runnable) {
        return scheduleTask(module, runnable, null, 0, 0, false);
    }

    @Override
    public ScheduledTask runTaskLater(@NotNull MbModule module, Runnable runnable, @Nullable TimeUnit timeUnit, long delayMillis) {
        return scheduleTask(module, runnable, timeUnit, delayMillis, 0, false);
    }

    @Override
    public ScheduledTask runTaskTimer(@NotNull MbModule module, Runnable runnable, @Nullable TimeUnit timeUnit, long delayMillis, long periodMillis) {
        return scheduleTask(module, runnable, timeUnit, delayMillis, periodMillis, false);
    }

    @Override
    public ScheduledTask runTaskAsync(@NotNull MbModule module, Runnable runnable) {
        return scheduleTask(module, runnable, null, 0, 0, true);
    }

    @Override
    public ScheduledTask runTaskLaterAsync(@NotNull MbModule module, Runnable runnable, @Nullable TimeUnit timeUnit, long delayMillis) {
        return scheduleTask(module, runnable, timeUnit, delayMillis, 0, true);
    }

    @Override
    public ScheduledTask runTaskTimerAsync(@NotNull MbModule module, Runnable runnable, @Nullable TimeUnit timeUnit, long delayMillis, long periodMillis) {
        return scheduleTask(module, runnable, timeUnit, delayMillis, periodMillis, true);
    }

    private ScheduledTask scheduleTask(@NotNull MbModule module, Runnable runnable, @Nullable TimeUnit timeUnit, long delayMillis, long periodMillis, boolean async) {
        int taskId = taskIdCounter.incrementAndGet();
        ScheduledTask task = new ScheduledTask(taskId, module, runnable, async, periodMillis > 0);
        tasks.put(taskId, task);

        Runnable wrappedTask = () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                logger.error(String.format("Task #%d threw an error while executing.", taskId));
                e.printStackTrace();
            }

            if (!task.isRepeating()) {
                tasks.remove(taskId);
            }
        };

        ScheduledFuture<?> future;
        Runnable finalRunnable = async ? () -> asyncExecutor.execute(wrappedTask) : wrappedTask;

        if (periodMillis > 0) {
            future = scheduler.scheduleAtFixedRate(
                    finalRunnable,
                    delayMillis,
                    periodMillis,
                    timeUnit == null ? TimeUnit.SECONDS : timeUnit
            );
        } else {
            future = scheduler.schedule(
                    finalRunnable,
                    delayMillis,
                    timeUnit == null ? TimeUnit.SECONDS : timeUnit
            );
        }

        task.setFuture(future);
        return task;
    }

    @Override
    public boolean cancelTask(int taskId) {
        ScheduledTask task = tasks.remove(taskId);
        if (task == null) return false;

        task.cancel();
        return true;
    }

    public void shutdown() {
        tasks.values().forEach(ScheduledTask::cancel);
        tasks.clear();

        scheduler.shutdown();
        asyncExecutor.shutdown();
    }

}
