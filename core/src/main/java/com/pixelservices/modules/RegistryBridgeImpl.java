package com.pixelservices.modules;

import com.pixelservices.api.addons.SlashCommandAddon;
import com.pixelservices.api.modules.RegistryBridge;
import com.pixelservices.commands.CommandManager;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class RegistryBridgeImpl implements RegistryBridge {
    private final CommandManager commandManager;

    public RegistryBridgeImpl(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void registerCommand(CommandData commandData, SlashCommandAddon slashCommandAddon) {
        commandManager.registerCommand(commandData, slashCommandAddon);
    }
}
