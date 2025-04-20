package com.pixelservices.modules;

import com.pixelservices.api.addons.SlashCommandAddon;
import com.pixelservices.api.modules.RegistryBridge;
import com.pixelservices.commands.CommandManager;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import com.pixelservices.api.commands.SlashCommandHandler;

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
     * Registers a command with the specified CommandData and SlashCommandAddon.
     *
     * @deprecated This method is deprecated and will be removed in future versions.
     * @param commandData       the CommandData representing the command to be registered
     * @param slashCommandAddon the SlashCommandAddon to be associated with the command
     */
    @Override
    @Deprecated(forRemoval = true)
    public void registerCommand(CommandData commandData, SlashCommandAddon slashCommandAddon) {
        commandManager.registerCommand(commandData, slashCommandAddon);
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
