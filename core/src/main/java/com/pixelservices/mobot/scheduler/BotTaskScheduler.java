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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BotTaskScheduler implements TaskScheduler {

    // This makes it tick 20 times per second, like Minecraft, this is in MS
    private static final long TICK_RATE = 50;

    private final Logger logger;

    private final Map<Integer, ScheduledTask> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger taskIdCounter = new AtomicInteger(0);
    private final ExecutorService asyncExecutor;
    private final ScheduledExecutorService tickScheduler;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private long currentTick = 0;
    private final Thread mainThread;

    public BotTaskScheduler() {
        this.mainThread = Thread.currentThread();

        this.logger = LoggerFactory.getLogger("Console");

        this.asyncExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(@NotNull Runnable runnable) {
                Thread thread = new Thread(runnable, "TaskScheduler-AsyncWorker-" + counter.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });

        this.tickScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "TaskScheduler-TickThread");
            thread.setDaemon(true);
            return thread;
        });

        startTickSystem();
    }

    /**
     * Starts the tick system which drives the scheduler.
     */
    private void startTickSystem() {
        tickScheduler.scheduleAtFixedRate(() -> {
            if (!isRunning.get()) {
                return;
            }

            try {
                processScheduledTasks();

                currentTick++;
            } catch (Exception e) {
                System.err.println("Error in tick processing:");
                e.printStackTrace();
            }
        }, 0, TICK_RATE, TimeUnit.MILLISECONDS);
    }

    /**
     * Processes all scheduled tasks that are due to run.
     */
    private void processScheduledTasks() {
        Map<Integer, ScheduledTask> tasksCopy = new ConcurrentHashMap<>(tasks);

        for (ScheduledTask task : tasksCopy.values()) {
            if (task.isCancelled()) {
                tasks.remove(task.getTaskId());
                continue;
            }

            if (task.shouldRunAtTick(currentTick)) {
                if (task.isAsync()) {
                    asyncExecutor.execute(() -> executeTask(task));
                } else {
                    executeTask(task);
                }

                if (!task.isRepeating()) {
                    tasks.remove(task.getTaskId());
                }
            }
        }
    }

    /**
     * Executes a task and handles any exceptions.
     */
    private void executeTask(ScheduledTask task) {
        try {
            task.getRunnable().run();
        } catch (Exception e) {
            logger.error(String.format("Task #%s threw an error while executing.", task.getTaskId()));
            e.printStackTrace();
        }
    }

    @Override
    public long getTickRate() {
        return TICK_RATE;
    }

    @Override
    public Map<Integer, ScheduledTask> getTasks() {
        return Collections.unmodifiableMap(tasks);
    }

    @Override
    public ScheduledTask runTask(@NotNull MbModule module,  Runnable runnable) {
        return scheduleTask(module, runnable, 0, 0, false);
    }

    @Override
    public ScheduledTask runTaskLater(@NotNull MbModule module,  Runnable runnable, long delay) {
        return scheduleTask(module, runnable, delay, 0, false);
    }

    @Override
    public ScheduledTask runTaskTimer(@NotNull MbModule module,  Runnable runnable, long delay, long period) {
        return scheduleTask(module, runnable, delay, period, false);
    }

    @Override
    public ScheduledTask runTaskAsync(@NotNull MbModule module,  Runnable runnable) {
        return scheduleTask(module, runnable, 0, 0, true);
    }

    @Override
    public ScheduledTask runTaskLaterAsync(@NotNull MbModule module,  Runnable runnable, long delay) {
        return scheduleTask(module, runnable, delay, 0, true);
    }

    @Override
    public ScheduledTask runTaskTimerAsync(@NotNull MbModule module,  Runnable runnable, long delay, long period) {
        return scheduleTask(module, runnable, delay, period, true);
    }

    /**
     * Schedules a task with the given parameters.
     */
    private ScheduledTask scheduleTask(@NotNull MbModule module,  Runnable runnable, long delay, long period, boolean async) {
        int taskId = taskIdCounter.incrementAndGet();
        long nextRun = currentTick + Math.max(0, delay);

        ScheduledTask task = new ScheduledTask(taskId, module, runnable, nextRun, period, async);
        tasks.put(taskId, task);

        return task;
    }

    @Override
    public boolean cancelTask(int taskId) {
        ScheduledTask task = tasks.get(taskId);

        if(task == null) {
            return false;
        }

        task.cancel();
        tasks.remove(taskId);
        return true;
    }

    /**
     * Cancels all tasks.
     */
    public void cancelAllTasks() {
        tasks.clear();
    }

    /**
     * Gets the current tick count.
     * @return The current tick
     */
    public long getCurrentTick() {
        return currentTick;
    }

    /**
     * Checks if the current thread is the main thread.
     * @return true if called from the main thread
     */
    public boolean isMainThread() {
        return Thread.currentThread() == mainThread;
    }

    public void shutdown() {
        isRunning.set(false);
        cancelAllTasks();

        tickScheduler.shutdown();
        asyncExecutor.shutdown();
    }

}
