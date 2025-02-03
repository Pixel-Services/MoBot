package com.pixelservices.modules;

import com.pixelservices.api.PrimitiveBotEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MbModule {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Logger getLogger() {
        return logger;
    }

    public void preEnable(PrimitiveBotEnvironment environment) {
        // Default implementation
    }

    public void onEnable() {
        // Default implementation
    }

    public void preDisable() {
        // Default implementation
    }

    public void onDisable() {
        // Default implementation
    }
}