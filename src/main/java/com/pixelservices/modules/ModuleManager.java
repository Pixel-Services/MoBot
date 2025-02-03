package com.pixelservices.modules;

import com.pixelservices.api.env.BotEnvironment;
import com.pixelservices.api.env.PrimitiveBotEnvironment;
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

    public void preEnable(PrimitiveBotEnvironment primitiveBotEnvironment) {
        AtomicInteger failedCount = new AtomicInteger();

        getPlugins().forEach(pluginWrapper -> {
            try {
                if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                    MbModule module = (MbModule) pluginWrapper.getPlugin();
                    module.injectPrimitiveBotEnvironment(primitiveBotEnvironment);
                    module.preEnable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during pre-enable", e);
                failedCount.getAndIncrement();
                unloadPlugin(pluginWrapper.getPluginDescriptor().getPluginId());
            }
        });

        logger.info("Successfully pre-enabled " + (getPlugins().stream().filter(plugin -> plugin.getState().equals(PluginState.LOADED)).count() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
    }

    public void enable(BotEnvironment botEnvironment) {
        AtomicInteger failedCount = new AtomicInteger();

        getPlugins().forEach(pluginWrapper -> {
            try {
                if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                    MbModule module = (MbModule) pluginWrapper.getPlugin();
                    module.injectBotEnvironment(botEnvironment);
                    module.onEnable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during enable", e);
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully enabled " + (getPlugins().stream().filter(plugin -> plugin.getState().equals(PluginState.LOADED)).count() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
    }

    public void preDisable() {
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