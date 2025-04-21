package com.pixelservices.mobot.api.scheduler;

import com.pixelservices.mobot.api.modules.MbModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface TaskScheduler {

    Map<Integer, ScheduledTask> getTasks();

    ScheduledTask runTask(@NotNull MbModule module, Runnable runnable);

    ScheduledTask runTaskLater(@NotNull MbModule module, Runnable runnable, @Nullable TimeUnit timeUnit, long delay);

    ScheduledTask runTaskTimer(@NotNull MbModule module,  Runnable runnable, @Nullable TimeUnit timeUnit, long delay, long period);

    ScheduledTask runTaskAsync(@NotNull MbModule module,  Runnable runnable);

    ScheduledTask runTaskLaterAsync(@NotNull MbModule module,  Runnable runnable, @Nullable TimeUnit timeUnit, long delay);

    ScheduledTask runTaskTimerAsync(@NotNull MbModule module,  Runnable runnable, @Nullable TimeUnit timeUnit, long delay, long period);

    boolean cancelTask(int taskId);

}
