package com.pixelservices.mobot.api.scheduler;

import com.pixelservices.mobot.api.modules.MbModule;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface TaskScheduler {

    long getTickRate();

    Map<Integer, ScheduledTask> getTasks();

    ScheduledTask runTask(@NotNull MbModule module,  Runnable runnable);

    ScheduledTask runTaskLater(@NotNull MbModule module, Runnable runnable, long delay);

    ScheduledTask runTaskTimer(@NotNull MbModule module,  Runnable runnable, long delay, long period);

    ScheduledTask runTaskAsync(@NotNull MbModule module,  Runnable runnable);

    ScheduledTask runTaskLaterAsync(@NotNull MbModule module,  Runnable runnable, long delay);

    ScheduledTask runTaskTimerAsync(@NotNull MbModule module,  Runnable runnable, long delay, long period);

    boolean cancelTask(int taskId);

}
