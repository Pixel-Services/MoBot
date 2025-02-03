package com.pixelservices.modules;

import com.pixelservices.api.BotEnvironment;
import com.pixelservices.api.PrimitiveBotEnvironment;
import com.pixelservices.plugin.Plugin;

public class MbModule extends Plugin {
    private PrimitiveBotEnvironment botEnvironment;

    final void injectPrivateBotEnvironment(PrimitiveBotEnvironment botEnvironment) {
        this.botEnvironment = botEnvironment;
    }

    final void injectBotEnvironment(BotEnvironment botEnvironment) {
        this.botEnvironment = botEnvironment;
    }

    public void preEnable() {

    }

    public void onEnable() {

    }

    public void preDisable() {

    }

    public void onDisable() {

    }

    public PrimitiveBotEnvironment getBotEnvironment() {
        return botEnvironment;
    }
}
