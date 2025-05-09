package com.pixelservices.mobot.api.modules;

import com.pixelservices.mobot.api.env.FinalizedBotEnvironment;
import com.pixelservices.mobot.api.env.PrimitiveBotEnvironment;

public interface ModuleRegistry {

    void preEnable(PrimitiveBotEnvironment primitiveBotEnvironment);

    void enable(FinalizedBotEnvironment finalizedBotEnvironment);

    void preDisable();

    void disable();

    void enable(MbModule module);

    void reload(MbModule module);

    void disable(MbModule module);

    MbModule getModule(String id);

    ModuleState getState(MbModule module);

}
