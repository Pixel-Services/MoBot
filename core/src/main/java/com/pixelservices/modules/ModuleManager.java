package com.pixelservices.modules;

import com.pixelservices.api.env.FinalizedBotEnvironment;
import com.pixelservices.api.env.PrimitiveBotEnvironment;
import com.pixelservices.api.modules.MbModule;
import com.pixelservices.commands.CommandManager;
import com.pixelservices.plugin.PluginWrapper;
import com.pixelservices.plugin.descriptor.finder.YamlDescriptorFinder;
import com.pixelservices.plugin.lifecycle.PluginState;
import com.pixelservices.plugin.manager.AbstractPluginManager;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ModuleManager extends AbstractPluginManager {
    public ModuleManager() {
        super(Paths.get("modules"), new YamlDescriptorFinder("module.yml"));
    }

    public void preEnable(PrimitiveBotEnvironment primitiveBotEnvironment, CommandManager commandManager) {
        if (getPlugins().isEmpty()) {
            logger.warn("No modules found in the modules directory.");
            return;
        }

        AtomicInteger failedCount = new AtomicInteger();

        getPlugins().forEach(pluginWrapper -> {
            try {
                if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                    MbModule module = (MbModule) pluginWrapper.getPlugin();
                    module.inject(primitiveBotEnvironment, new RegistryBridgeImpl(commandManager));
                    module.preEnable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during pre-enable", e);
                logger.error("Unloading " + pluginWrapper.getPluginDescriptor().getPluginId() + " due to exception during pre-enable.");
                pluginWrapper.unload();
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully pre-enabled " + (getPlugins().stream().filter(plugin -> plugin.getState().equals(PluginState.LOADED)).count() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
    }

    public void enable(FinalizedBotEnvironment finalizedBotEnvironment) {
        if (getPlugins().isEmpty()) {
            return;
        }

        AtomicInteger failedCount = new AtomicInteger();

        getPlugins().forEach(pluginWrapper -> {
            try {
                if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                    MbModule module = (MbModule) pluginWrapper.getPlugin();
                    module.finalizeBotEnvironment(finalizedBotEnvironment);
                    module.onEnable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during enable", e);
                logger.error("Unloading " + pluginWrapper.getPluginDescriptor().getPluginId() + " due to exception during enable.");
                pluginWrapper.unload();
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully enabled " + (getPlugins().stream().filter(plugin -> plugin.getState().equals(PluginState.LOADED)).count() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
    }

    public void preDisable() {
        if (getPlugins().isEmpty()) {
            return;
        }

        AtomicInteger failedCount = new AtomicInteger();

        getPlugins().forEach(pluginWrapper -> {
            try {
                if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                    MbModule module = (MbModule) pluginWrapper.getPlugin();
                    module.preDisable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during pre-disable", e);
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully pre-disabled " + (getPlugins().stream().filter(plugin -> plugin.getState().equals(PluginState.LOADED)).count() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
    }

    public void disable() {
        if (getPlugins().isEmpty()) {
            return;
        }

        AtomicInteger failedCount = new AtomicInteger();

        getPlugins().forEach(pluginWrapper -> {
            try {
                if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                    MbModule module = (MbModule) pluginWrapper.getPlugin();
                    module.onDisable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during disable", e);
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully disabled " + (getPlugins().stream().filter(plugin -> plugin.getState().equals(PluginState.LOADED)).count() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");

        unloadPlugins();
    }

    public List<PluginWrapper> getModules() {
        return getPlugins();
    }
}