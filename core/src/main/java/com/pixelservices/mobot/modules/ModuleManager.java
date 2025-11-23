package com.pixelservices.mobot.modules;

import com.pixelservices.mobot.api.env.FinalizedBotEnvironment;
import com.pixelservices.mobot.api.env.PrimitiveBotEnvironment;
import com.pixelservices.mobot.api.modules.MbModule;
import com.pixelservices.mobot.api.modules.ModuleRegistry;
import com.pixelservices.mobot.api.modules.ModuleState;
import com.pixelservices.mobot.api.scheduler.TaskScheduler;
import com.pixelservices.mobot.commands.CommandManager;
import dev.siea.jonion.PluginWrapper;
import dev.siea.jonion.descriptor.finder.YamlDescriptorFinder;
import dev.siea.jonion.lifecycle.PluginState;
import dev.siea.jonion.manager.AbstractPluginManager;

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
                logger.error("{} threw an exception during pre-enable", pluginWrapper.getPluginDescriptor().getPluginId(), e);
                logger.error("Unloading {} due to exception during pre-enable.", pluginWrapper.getPluginDescriptor().getPluginId());
                pluginWrapper.unload();
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully pre-enabled {} modules. {} Modules failed this phase.", moduleStates.values().stream().filter(moduleState -> moduleState == ModuleState.PENDING_ENABLE).count() - failedCount.get(), failedCount.get());
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
                logger.error("{} threw an exception during enable", pluginWrapper.getPluginDescriptor().getPluginId(), e);
                logger.error("Unloading {} due to exception during enable.", pluginWrapper.getPluginDescriptor().getPluginId());
                pluginWrapper.unload();
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully enabled {} modules. {} Modules failed this phase.", moduleStates.values().stream().filter(moduleState -> moduleState == ModuleState.ENABLED).count() - failedCount.get(), failedCount.get());
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
                logger.error("{} threw an exception during pre-disable", pluginWrapper.getPluginDescriptor().getPluginId(), e);
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully pre-disabled {} modules. {} Modules failed this phase.", moduleStates.values().stream().filter(moduleState -> moduleState == ModuleState.PENDING_DISABLE).count() - failedCount.get(), failedCount.get());
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
                logger.error("{} threw an exception during disable", pluginWrapper.getPluginDescriptor().getPluginId(), e);
                failedCount.getAndIncrement();
            }
        });

        logger.info("Successfully disabled {} modules. {} Modules failed this phase.", moduleStates.values().stream().filter(moduleState -> moduleState == ModuleState.DISABLED).count() - failedCount.get(), failedCount.get());

        unloadPlugins();
    }

    @Override
    public void enable(MbModule module) {
        if(finalizedBotEnvironment == null) {
            logger.error("Unable to enable module {}, bot isn't initialized yet.", module.getId());
            return;
        }

        if(getState(module) == ModuleState.INVALID) {
            logger.error("Unable to enable invalid module {}, was it never initially registered?", module.getId());
            return;
        }

        PluginWrapper pluginWrapper = module.getPluginWrapper();

        if (!pluginWrapper.getState().equals(PluginState.LOADED)) {
            logger.error("Unable to enable module {}, it has not been loaded.", module.getId());
            return;
        }

        if(getState(module) == ModuleState.PENDING_ENABLE) {
            logger.warn("Unable to enable module {}, it's already being enabled!", module.getId());
            return;
        }

        if(getState(module) == ModuleState.ENABLED) {
            logger.warn("Unable to enable module {}, it's already enabled!", module.getId());
            return;
        }

        try {
            if (pluginWrapper.getState().equals(PluginState.LOADED)) {
                module.finalizeBotEnvironment(finalizedBotEnvironment);
                module.listenerBridge(new ListenerBridgeImpl(finalizedBotEnvironment));
                module.onEnable();
            } else {
                logger.error("Failed to enable module {}, it is not loaded.", module.getId());
                return;
            }
        } catch (Throwable e) {
            logger.error("{} threw an exception during enable", pluginWrapper.getPluginDescriptor().getPluginId(), e);
            logger.error("Unloading {} due to exception during enable.", pluginWrapper.getPluginDescriptor().getPluginId());
            pluginWrapper.unload();
            moduleStates.put(module.getId(), ModuleState.INVALID);
            return;
        }

        moduleStates.put(module.getId(), ModuleState.ENABLED);
        logger.info("Module {} has been enabled!", module.getId());
    }

    @Override
    public void reload(MbModule module) {
        if(getState(module) == ModuleState.INVALID) {
            logger.error("Failed to reload invalid module {}, was it never initially registered?", module.getId());
            return;
        }

        PluginWrapper pluginWrapper = module.getPluginWrapper();

        if (!pluginWrapper.getState().equals(PluginState.LOADED)) {
            logger.error("Failed to enable module {}, it is not loaded.", module.getId());
            return;
        }

        try {
            disable(module);
            module.getPluginWrapper().unload();
            module.getPluginWrapper().load();
            enable(module);
        } catch (Throwable e) {
            logger.error("{} threw an exception during reload", pluginWrapper.getPluginDescriptor().getPluginId(), e);
            return;
        }

        logger.info("Module {} has been reloaded!", module.getId());
    }

    @Override
    public void disable(MbModule module) {
        if(getState(module) == ModuleState.INVALID) {
            logger.error("Failed to disable invalid module {}, was it never initially registered?", module.getId());
            return;
        }

        PluginWrapper pluginWrapper = module.getPluginWrapper();

        if (!pluginWrapper.getState().equals(PluginState.LOADED)) {
            logger.error("Failed to enable module {}, it is not loaded.", module.getId());
            return;
        }

        if(getState(module) == ModuleState.PENDING_DISABLE) {
            logger.warn("Failed to disable module {}, it's already being disabled!", module.getId());
            return;
        }

        if(getState(module) == ModuleState.DISABLED) {
            logger.warn("Failed to disable module {}, it's already disabled!", module.getId());
            return;
        }

        try {
            if(finalizedBotEnvironment != null) {
                module.getListenerBridge().getListeners().forEach(listener -> finalizedBotEnvironment.getShardManager().removeEventListener(listener));
                module.getListenerBridge().getListeners().clear();
            }
            module.onDisable();
        } catch (Throwable e) {
            logger.error("{} threw an exception during disable", pluginWrapper.getPluginDescriptor().getPluginId(), e);
            logger.error("Unloading {} due to exception during disable.", pluginWrapper.getPluginDescriptor().getPluginId());
            pluginWrapper.unload();
            moduleStates.put(module.getId(), ModuleState.INVALID);
            return;
        }

        moduleStates.put(module.getId(), ModuleState.DISABLED);
        logger.info("Module {} has been disabled!", module.getId());
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