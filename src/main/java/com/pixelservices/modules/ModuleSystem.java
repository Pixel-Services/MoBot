package com.pixelservices.modules;

import com.pixelservices.api.PrimitiveBotEnvironment;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

public class ModuleSystem {
    private final Logger logger;
    private final ModuleManager moduleManager;

    public ModuleSystem() {
        logger = LoggerFactory.getLogger("ModuleSystem");
        moduleManager = new ModuleManager();
        createModulesDirectory();
        loadModules();
    }

    private void createModulesDirectory() {
        File modulesDir = new File("modules");
        if (!modulesDir.exists()) {
            boolean created = modulesDir.mkdirs();
            if (created) {
                logger.info("Created modules directory.");
            } else {
                logger.error("Failed to create modules directory.");
            }
        }
    }

    private void loadModules() {
        moduleManager.loadPlugins();
    }

    public void preEnable(PrimitiveBotEnvironment primitiveBotEnvironment) {
        moduleManager.preEnable(primitiveBotEnvironment);
    }

    public void onEnable() {
        moduleManager.startPlugins();
    }

    public void preDisable() {
        moduleManager.preDisable();
    }

    public void onDisable() {
        moduleManager.stopPlugins();
    }

    public PluginManager getModuleManager() {
        return moduleManager;
    }
}