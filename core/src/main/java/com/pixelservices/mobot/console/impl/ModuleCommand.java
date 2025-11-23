package com.pixelservices.mobot.console.impl;


import com.pixelservices.mobot.api.modules.MbModule;
import com.pixelservices.mobot.api.modules.ModuleState;
import com.pixelservices.mobot.api.scheduler.ScheduledTask;
import com.pixelservices.mobot.api.scheduler.TaskScheduler;
import com.pixelservices.mobot.console.ConsoleCommand;
import com.pixelservices.mobot.modules.ModuleManager;
import dev.siea.jonion.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ModuleCommand implements ConsoleCommand {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ModuleCommand.class);
    private final TaskScheduler taskScheduler;
    private final ModuleManager moduleManager;

    public ModuleCommand(TaskScheduler taskScheduler, ModuleManager moduleManager) {
        this.taskScheduler = taskScheduler;
        this.moduleManager = moduleManager;
    }

    @Override
    public void execute(String[] args, Logger logger) {
        if(args.length == 0) {
            logger.info("Module Command Usage:");
            logger.info("module list - List all modules.");
            logger.info("module reload <module> - Reload a module");
            logger.info("module tasks <module> - List a modules scheduled tasks.");
            logger.info("module enable <module> - Attempt to enable a module.");
            logger.info("module disable <module> - Attempt to disable a module.");
            return;
        }

        if(args[0].equalsIgnoreCase("list")) {
            List<PluginWrapper> wrappers = moduleManager.getModules();

            if (wrappers.isEmpty()) {
                logger.info("No modules loaded.");
                return;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("Modules: ");

            for (PluginWrapper module : wrappers) {
                String moduleId = module.getPluginDescriptor().getPluginId();
                String statusColor = moduleManager.getModuleStates().getOrDefault(moduleId, ModuleState.INVALID) == ModuleState.ENABLED ? "\u001B[32m" : "\u001B[31m";
                builder.append(statusColor).append(moduleId).append("\u001B[0m").append(", ");
            }

            builder.setLength(builder.length() - 2);
            logger.info(builder.toString());
            return;
        }

        if(args[0].equalsIgnoreCase("tasks")) {
            if(args.length < 2) {
                logger.warn("You must provide a module id!");
                return;
            }

            MbModule module = moduleManager.getModule(args[1]);

            if(module == null) {
                logger.error("Invalid module!");
                return;
            }

            List<ScheduledTask> moduleTasks = taskScheduler.getTasks().values().stream()
                    .filter(scheduledTask -> scheduledTask.getModule().getId().equalsIgnoreCase(module.getId())).toList();

            if(moduleTasks.isEmpty()) {
                logger.info(String.format("Module %s has no scheduled tasks.", module.getId()));
                return;
            }

            logger.info("{} Scheduled Tasks:", module.getId());
            logger.info("Total: {}, Async: {}, Repeating: {}", moduleTasks.size(), moduleTasks.stream().filter(ScheduledTask::isAsync).count(), moduleTasks.stream().filter(ScheduledTask::isRepeating).count());
            return;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            if(args.length < 2) {
                logger.warn("You must provide a module id!");
                return;
            }

            MbModule module = moduleManager.getModule(args[1]);

            if(module == null) {
                logger.error("Invalid module!");
                return;
            }

            moduleManager.reload(module);
        }

        if(args[0].equalsIgnoreCase("enable")) {
            if(args.length < 2) {
                logger.warn("You must provide a module id!");
                return;
            }

            MbModule module = moduleManager.getModule(args[1]);

            if(module == null) {
                logger.error("Invalid module!");
                return;
            }

            moduleManager.enable(module);
        }

        if(args[0].equalsIgnoreCase("disable")) {
            if(args.length < 2) {
                logger.warn("You must provide a module id!");
                return;
            }

            MbModule module = moduleManager.getModule(args[1]);

            if(module == null) {
                logger.error("Invalid module!");
                return;
            }

            moduleManager.disable(module);
        }
    }
}
