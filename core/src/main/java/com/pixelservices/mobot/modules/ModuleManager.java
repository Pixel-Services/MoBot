package com.pixelservices.mobot.modules;

import com.pixelservices.mobot.api.env.FinalizedBotEnvironment;
import com.pixelservices.mobot.api.env.PrimitiveBotEnvironment;
import com.pixelservices.mobot.api.modules.MbModule;
import com.pixelservices.mobot.api.modules.ModuleRegistry;
import com.pixelservices.mobot.api.modules.ModuleState;
import com.pixelservices.mobot.api.scheduler.TaskScheduler;
import com.pixelservices.mobot.commands.CommandManager;
import com.pixelservices.plugin.PluginWrapper;
import com.pixelservices.plugin.descriptor.finder.YamlDescriptorFinder;
import com.pixelservices.plugin.lifecycle.PluginState;
import com.pixelservices.plugin.manager.AbstractPluginManager;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ModuleManager extends AbstractPluginManager implements ModuleRegistry {

    private final Map<String, ModuleState> moduleStates = new HashMap<>();

    private final TaskScheduler taskScheduler;
    private final CommandManager commandManager;

    private FinalizedBotEnvironment finalizedBotEnvironment;

    public ModuleManager(TaskScheduler taskScheduler, CommandManager commandManager) {
        super(Paths.get("modules"), new YamlDescriptorFinder("module.yml"));

        this.taskScheduler = taskScheduler;
        this.commandManager = commandManager;
    }

    public void preEnable(PrimitiveBotEnvironment primitiveBotEnvironment) {
        if (getPlugins().isEmpty()) {
            logger.warn("No modules found in the modules directory.");
            return;
        }

        AtomicInteger failedCount = new AtomicInteger();

        getPlugins().forEach(pluginWrapper -> {
            try {
                if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                    MbModule module = (MbModule) pluginWrapper.getPlugin();
                    module.inject(taskScheduler, this, primitiveBotEnvironment, new RegistryBridgeImpl(commandManager));

                    moduleStates.put(module.getId(), ModuleState.PENDING_ENABLE);

                    module.preEnable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during pre-enable", e);
                logger.error("Unloading " + pluginWrapper.getPluginDescriptor().getPluginId() + " due to exception during pre-enable.");
                pluginWrapper.unload();
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully pre-enabled " + (moduleStates.values().stream().filter(moduleState -> moduleState == ModuleState.PENDING_ENABLE).count() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
    }

    public void enable(FinalizedBotEnvironment finalizedBotEnvironment) {
        if (getPlugins().isEmpty()) {
            return;
        }

        this.finalizedBotEnvironment = finalizedBotEnvironment;

        AtomicInteger failedCount = new AtomicInteger();

        getPlugins().forEach(pluginWrapper -> {
            try {
                if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                    MbModule module = (MbModule) pluginWrapper.getPlugin();
                    module.finalizeBotEnvironment(finalizedBotEnvironment);
                    module.listenerBridge(new ListenerBridgeImpl(finalizedBotEnvironment));

                    moduleStates.put(module.getId(), ModuleState.ENABLED);

                    module.onEnable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during enable", e);
                logger.error("Unloading " + pluginWrapper.getPluginDescriptor().getPluginId() + " due to exception during enable.");
                pluginWrapper.unload();
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully enabled " + (moduleStates.values().stream().filter(moduleState -> moduleState == ModuleState.ENABLED).count() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
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

                    moduleStates.put(module.getId(), ModuleState.PENDING_DISABLE);

                    module.preDisable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during pre-disable", e);
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully pre-disabled " + (moduleStates.values().stream().filter(moduleState -> moduleState == ModuleState.PENDING_DISABLE).count() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");
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

                    if(finalizedBotEnvironment != null) {
                        module.getListenerBridge().getListeners().forEach(listener -> finalizedBotEnvironment.getShardManager().removeEventListener(listener));
                    }

                    moduleStates.put(module.getId(), ModuleState.DISABLED);

                    module.onDisable();
                }
            } catch (Throwable e) {
                logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during disable", e);
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully disabled " + (moduleStates.values().stream().filter(moduleState -> moduleState == ModuleState.DISABLED).count() - failedCount.get()) + " modules. " + failedCount.get() + " Modules failed this phase.");

        unloadPlugins();
    }

    @Override
    public void enable(MbModule module) {
        if(finalizedBotEnvironment == null) {
            logger.error(String.format("Unable to enable module %s, bot isn't initialized yet.", module.getId()));
            return;
        }

        if(getState(module) == ModuleState.INVALID) {
            logger.error(String.format("Unable to enable invalid module %s, was it never initially registered?", module.getId()));
            return;
        }

        PluginWrapper pluginWrapper = module.getPluginWrapper();

        if (!pluginWrapper.getState().equals(PluginState.LOADED)) {
            logger.error(String.format("Unable to enable module %s, it has not been loaded.", module.getId()));
            return;
        }

        if(getState(module) == ModuleState.PENDING_ENABLE) {
            logger.warn(String.format("Unable to enable module %s, it's already being enabled!", module.getId()));
            return;
        }

        if(getState(module) == ModuleState.ENABLED) {
            logger.warn(String.format("Unable to enable module %s, it's already enabled!", module.getId()));
            return;
        }

        try {
            if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                module.finalizeBotEnvironment(finalizedBotEnvironment);
                module.listenerBridge(new ListenerBridgeImpl(finalizedBotEnvironment));
                module.onEnable();
            } else {
                logger.error(String.format("Failed to enable module %s, it is not loaded.", module.getId()));
                return;
            }
        } catch (Throwable e) {
            logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during enable", e);
            logger.error("Unloading " + pluginWrapper.getPluginDescriptor().getPluginId() + " due to exception during enable.");
            pluginWrapper.unload();
            moduleStates.put(module.getId(), ModuleState.INVALID);
            return;
        }

        moduleStates.put(module.getId(), ModuleState.ENABLED);
        logger.info(String.format("Module %s has been enabled!", module.getId()));
    }

    @Override
    public void reload(MbModule module) {
        if(getState(module) == ModuleState.INVALID) {
            logger.error(String.format("Failed to reload invalid module %s, was it never initially registered?", module.getId()));
            return;
        }

        PluginWrapper pluginWrapper = module.getPluginWrapper();

        if (!pluginWrapper.getState().equals(PluginState.LOADED)) {
            logger.error(String.format("Failed to enable module %s, it is not loaded.", module.getId()));
            return;
        }

        try {
            disable(module);
            module.getPluginWrapper().unload();
            module.getPluginWrapper().load();
            enable(module);
        } catch (Throwable e) {
            logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during reload", e);
            return;
        }

        logger.info(String.format("Module %s has been reloaded!", module.getId()));
    }

    @Override
    public void disable(MbModule module) {
        if(getState(module) == ModuleState.INVALID) {
            logger.error(String.format("Failed to disable invalid module %s, was it never initially registered?", module.getId()));
            return;
        }

        PluginWrapper pluginWrapper = module.getPluginWrapper();

        if (!pluginWrapper.getState().equals(PluginState.LOADED)) {
            logger.error(String.format("Failed to enable module %s, it is not loaded.", module.getId()));
            return;
        }

        if(getState(module) == ModuleState.PENDING_DISABLE) {
            logger.warn(String.format("Failed to disable module %s, it's already being disabled!", module.getId()));
            return;
        }

        if(getState(module) == ModuleState.DISABLED) {
            logger.warn(String.format("Failed to disable module %s, it's already disabled!", module.getId()));
            return;
        }

        try {
            if(finalizedBotEnvironment != null) {
                module.getListenerBridge().getListeners().forEach(listener -> finalizedBotEnvironment.getShardManager().removeEventListener(listener));
                module.getListenerBridge().getListeners().clear();
            }
            module.onDisable();
        } catch (Throwable e) {
            logger.error(pluginWrapper.getPluginDescriptor().getPluginId() + " threw an exception during disable", e);
            logger.error("Unloading " + pluginWrapper.getPluginDescriptor().getPluginId() + " due to exception during disable.");
            pluginWrapper.unload();
            moduleStates.put(module.getId(), ModuleState.INVALID);
            return;
        }

        moduleStates.put(module.getId(), ModuleState.DISABLED);
        logger.info(String.format("Module %s has been disabled!", module.getId()));
    }

    @Override
    public MbModule getModule(String id) {
        return getPlugins().stream().filter(pluginWrapper -> pluginWrapper.getPluginDescriptor().getPluginId().equalsIgnoreCase(id))
                .map(pluginWrapper -> (MbModule) pluginWrapper.getPlugin()).findFirst().orElse(null);
    }

    public List<PluginWrapper> getModules() {
        return getPlugins();
    }

    public Map<String, ModuleState> getModuleStates() {
        return moduleStates;
    }

    @Override
    public ModuleState getState(MbModule module) {
        return moduleStates.getOrDefault(module.getId(), ModuleState.INVALID);
    }
}