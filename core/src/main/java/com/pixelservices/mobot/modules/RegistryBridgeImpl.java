package com.pixelservices.mobot.modules;

import com.pixelservices.mobot.api.modules.RegistryBridge;
import com.pixelservices.mobot.commands.CommandManager;
import com.pixelservices.mobot.api.commands.SlashCommandHandler;

/**
 * This class serves as a bridge for registering commands and command handlers.
 * It is intended to be used by modules to register their commands and command handlers.
 */
public class RegistryBridgeImpl implements RegistryBridge {
    private final CommandManager commandManager;

    /**
     * Constructor for RegistryBridgeImpl.
     *
     * @param commandManager the CommandManager instance to be used for registering commands and command handlers
     */
    public RegistryBridgeImpl(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Registers a command handler with the specified SlashCommandHandler.
     *
     * @param slashCommandHandler the SlashCommandHandler to be registered
     */
    @Override
    public void registerCommandHandler(SlashCommandHandler slashCommandHandler) {
        commandManager.registerCommandHandler(slashCommandHandler);
    }
}
