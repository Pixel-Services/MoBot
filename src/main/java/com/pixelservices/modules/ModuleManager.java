package com.pixelservices.modules;

import com.pixelservices.api.PrimitiveBotEnvironment;
import com.pixelservices.plugin.descriptor.finder.YamlDescriptorFinder;
import com.pixelservices.plugin.lifecycle.PluginState;
import com.pixelservices.plugin.manager.AbstractPluginManager;

import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public class ModuleManager extends AbstractPluginManager {
    public ModuleManager() {
        super(Paths.get("modules"), new YamlDescriptorFinder());
    }

    public void preEnable(PrimitiveBotEnvironment primitiveBotEnvironment) {
        AtomicInteger failedCount = new AtomicInteger();

        getPlugins().forEach(pluginWrapper -> {
            try {
                if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                    MbModule module = (MbModule) pluginWrapper.getPlugin();
                    module.injectPrivateBotEnvironment(primitiveBotEnvironment);
                    module.preEnable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during pre-enable", e);
                failedCount.getAndIncrement();
                unloadPlugin(pluginWrapper.getPluginDescriptor().getPluginId());
            }
        });

        logger.info("Successfully pre-enabled " + (getPlugins().size() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
    }

    public void enable() {
        AtomicInteger failedCount = new AtomicInteger();

        getPlugins().forEach(pluginWrapper -> {
            try {
                if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                    MbModule module = (MbModule) pluginWrapper.getPlugin();
                    module.onEnable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during enable", e);
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully enabled " + (getPlugins().size() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
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

        logger.info("Successfully pre-disabled " + (getPlugins().size() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
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

        logger.info("Successfully disabled " + (getPlugins().size() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");

        unloadPlugins();
    }
}