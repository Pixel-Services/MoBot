package com.pixelservices.manager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import com.pixelservices.api.addons.SlashCommandAddon;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CommandManager is responsible for managing and registering slash commands within a Discord guild.
 * It handles the registration of commands when the bot joins a new guild or when the guild is ready.
 * It also processes interactions with slash commands.
 */
public class CommandManager extends ListenerAdapter {
    private final ArrayList<CommandData> commandDataList = new ArrayList<>();
    private final HashMap<String, SlashCommandAddon> commands = new HashMap<>();
    private final List<Guild> guilds = new ArrayList<>();

    /**
     * This method is called when the guild is fully loaded and ready.
     * It registers the slash commands with the guild.
     *
     * @param event the GuildReadyEvent containing information about the guild that is ready
     */
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        event.getGuild().updateCommands()
                .addCommands(commandDataList)
                .queue();
        if (!guilds.contains(event.getGuild())) { guilds.add(event.getGuild()); }
    }

    /**
     * This method is called when the bot joins a new guild.
     * It registers the slash commands with the new guild.
     *
     * @param event the GuildJoinEvent containing information about the guild the bot has joined
     */
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        event.getGuild().updateCommands()
                .addCommands(commandDataList)
                .queue();
        if (!guilds.contains(event.getGuild())) { guilds.add(event.getGuild()); }
    }

    /**
     * This method is called when a slash command interaction is received.
     * It delegates the command execution to the appropriate {@link SlashCommandAddon} based on the command name.
     *
     * @param event the SlashCommandInteractionEvent containing information about the received command interaction
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (String command : commands.keySet()) {
            if (command.equalsIgnoreCase(event.getName())) {
                commands.get(command).execute(event);
                break;
            }
        }
    }

    /**
     * Registers a custom {@link SlashCommandAddon} with the CommandManager.
     * The registered command will be handled in the {@link #onSlashCommandInteraction(SlashCommandInteractionEvent)} method.
     *
     * @param slashCommandAddon the {@link SlashCommandAddon} to register
     */
    public void registerCommand(CommandData commandData, SlashCommandAddon slashCommandAddon) {
        commands.put(commandData.getName(), slashCommandAddon);
        commandDataList.add(commandData);
        for (Guild guild : guilds) {
            guild.updateCommands()
                    .addCommands(commandDataList)
                    .queue();
        }
    }
}
