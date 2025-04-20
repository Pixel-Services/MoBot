package com.pixelservices.console.impl;

import com.pixelservices.api.modules.MbModule;
import com.pixelservices.api.modules.ModuleState;
import com.pixelservices.console.ConsoleCommand;
import com.pixelservices.logger.Logger;
import com.pixelservices.modules.ModuleManager;
import com.pixelservices.plugin.PluginWrapper;

import java.util.List;

public class ModuleCommand implements ConsoleCommand {
    private final ModuleManager moduleManager;

    public ModuleCommand(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public void execute(String[] args, Logger logger) {
        if(args.length == 0) {
            logger.info("Module Command Usage:");
            logger.info("module list - List all modules.");
            logger.info("module reload <module> - Reload a module");
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
