package com.pixelservices.mobot.api.modules.listener;

import java.util.Set;

public interface ListenerBridge {

    void registerListener(ModuleListener listener);

    Set<ModuleListener> getListeners();

}
