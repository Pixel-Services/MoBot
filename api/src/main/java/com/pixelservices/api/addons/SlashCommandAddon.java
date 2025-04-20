package com.pixelservices.api.addons;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a command that can be executed in response to a slash command interaction in Discord.
 *
 * This interface is deprecated and should be replaced with the new command system.
 */
@Deprecated
public interface SlashCommandAddon {

    /**
     * Executes the command when a slash command interaction is received.
     * This method is called when the command is executed by a user.
     *
     * @param event the SlashCommandInteractionEvent containing information about the command interaction
     */
    void execute(@NotNull SlashCommandInteractionEvent event);
}