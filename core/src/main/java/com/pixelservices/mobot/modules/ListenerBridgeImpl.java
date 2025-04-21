package com.pixelservices.mobot.modules;

import com.pixelservices.mobot.api.env.FinalizedBotEnvironment;
import com.pixelservices.mobot.api.modules.listener.ListenerBridge;
import com.pixelservices.mobot.api.modules.listener.ModuleListener;

import java.util.HashSet;
import java.util.Set;

public class ListenerBridgeImpl implements ListenerBridge {

    private final Set<ModuleListener> listeners = new HashSet<>();

    private final FinalizedBotEnvironment botEnvironment;

    public ListenerBridgeImpl(FinalizedBotEnvironment botEnvironment) {
        this.botEnvironment = botEnvironment;
    }

    @Override
    public void registerListener(ModuleListener listener) {
        if(listeners.contains(listener)) {
            return;
        }

        listeners.add(listener);
        botEnvironment.getShardManager().addEventListener(listener);
    }

    @Override
    public Set<ModuleListener> getListeners() {
        return listeners;
    }

}
