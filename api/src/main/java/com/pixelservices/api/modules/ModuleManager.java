package com.pixelservices.api.modules;

import com.pixelservices.api.env.FinalizedBotEnvironment;
import com.pixelservices.api.env.PrimitiveBotEnvironment;

public interface ModuleManager {

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
