package com.pixelservices.api.console.impl;

import com.pixelservices.api.console.ConsoleCommand;
import com.pixelservices.logger.Logger;
import com.pixelservices.modules.ModuleManager;
import com.pixelservices.plugin.PluginWrapper;
import com.pixelservices.plugin.lifecycle.PluginState;

import java.util.List;

public class ModulesCommand implements ConsoleCommand {
    private final ModuleManager moduleManager;

    public ModulesCommand(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public void execute(String[] args, Logger logger) {
        List<PluginWrapper> wrappers = moduleManager.getModules();
        if (wrappers.isEmpty()) {
            logger.info("No modules loaded.");
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Modules: ");
        for (PluginWrapper module : wrappers) {
            String moduleId = module.getPluginDescriptor().getPluginId();
            String statusColor = module.getState().equals(PluginState.LOADED) ? "\u001B[32m" : "\u001B[31m";
            builder.append(statusColor).append(moduleId).append("\u001B[0m").append(", ");
        }
        builder.setLength(builder.length() - 2);
        logger.info(builder.toString());
    }
}
