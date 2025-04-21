package com.pixelservices.mobot.api.modules;

import com.pixelservices.mobot.api.commands.SlashCommandHandler;

/**
 * Interface for a registry bridge that allows modules to register commands and command handlers.
 * <p>
 * This interface is used to register commands and their corresponding handlers in the bot's command registry.
 * </p>
 */
public interface RegistryBridge {

    /**
     * Registers a command handler for a slash command.
     *
     * @param slashCommand The command handler that will handle the slash command.
     */
    void registerCommandHandler(SlashCommandHandler slashCommand);
}
