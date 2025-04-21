package com.pixelservices.mobot.api.modules;

import com.pixelservices.mobot.api.addons.SlashCommandAddon;
import com.pixelservices.mobot.api.commands.SlashCommandHandler;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * Interface for a registry bridge that allows modules to register commands and command handlers.
 * <p>
 * This interface is used to register commands and their corresponding handlers in the bot's command registry.
 * </p>
 */
public interface RegistryBridge {
    /**
     * Registers a command with the specified command data and command handler.
     *
     * @deprecated This method is deprecated and will be removed in future versions.
     * @param commandData        The command data representing the command to be registered.
     * @param slashCommandAddon  The command handler that will handle the command when executed.
     */
    @Deprecated(forRemoval = true)
    void registerCommand(CommandData commandData, SlashCommandAddon slashCommandAddon);

    /**
     * Registers a command handler for a slash command.
     *
     * @param slashCommand The command handler that will handle the slash command.
     */
    void registerCommandHandler(SlashCommandHandler slashCommand);
}
