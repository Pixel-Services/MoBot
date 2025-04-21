package com.pixelservices.mobot.commands;

import com.pixelservices.mobot.api.commands.SlashCommand;
import com.pixelservices.mobot.api.commands.SlashCommandArgument;
import com.pixelservices.mobot.api.commands.SlashCommandChoice;
import com.pixelservices.mobot.api.commands.SlashCommandHandler;
import com.pixelservices.mobot.exceptions.CommandExecuteException;
import com.pixelservices.logger.Logger;
import com.pixelservices.logger.LoggerFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CommandManager is responsible for managing and registering slash commands within a Discord guild.
 * It handles the registration of commands when the bot joins a new guild or when the guild is ready.
 * It also processes interactions with slash commands.
 */
public class CommandManager extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ArrayList<CommandData> commandDataList = new ArrayList<>();
    private final Map<String, SlashCommandExecutor> slashCommandExecutorMap = new HashMap<>();
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
     * It executes the corresponding command based on the command name.
     *
     * @param event the SlashCommandInteractionEvent containing information about the command interaction
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommandExecutor executor = slashCommandExecutorMap.get(event.getName());
        if (executor != null) {
            try {
                executor.execute(event);
            } catch (CommandExecuteException e) {
                logger.error("Failed to execute command: " + event.getName(), e);
            }
            return;
        }
    }

    /**
     * Registers a {@link SlashCommandHandler} with the CommandManager.
     * The registered commands will be handled in the {@link #onSlashCommandInteraction(SlashCommandInteractionEvent)} method.
     *
     * @param handler the {@link SlashCommandHandler} to register
     */
    public void registerCommandHandler(SlashCommandHandler handler) {
        Class<?> handlerClass = handler.getClass();
        for (Method method : handlerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SlashCommand.class)) {
                SlashCommand annotation = method.getAnnotation(SlashCommand.class);
                String commandName = annotation.name();

                if (commandExists(commandName)) {
                    logger.warn("Unable to register command: " + commandName + ". A command with this name has already been registered.");
                    continue;
                }

                List<CommandData> commandDataList = new ArrayList<>();

                //Setup the Command Executor
                SlashCommandArgument[] argumentAnnotations = method.getAnnotationsByType(SlashCommandArgument.class);
                List<SlashCommandArgument> arguments = List.of(argumentAnnotations);
                SlashCommandExecutor executor = new SlashCommandExecutor(handler, method, arguments);
                
                String description = annotation.description();
                Permission permission = annotation.permission();
                
                //Setup the Command
                SlashCommandData commandData = generateSlashCommandData(commandName, description, permission, argumentAnnotations);
                commandDataList.add(commandData);
                slashCommandExecutorMap.put(commandName, executor);
                
                //Setup the Aliases
                String[] aliases = annotation.aliases();
                for (String alias : aliases) {
                    if (slashCommandExecutorMap.containsKey(alias)) {
                        logger.warn("Unable to register alias: " + alias + ". A command with this name has already been registered.");
                        continue;
                    }
                    commandDataList.add(generateSlashCommandData(alias, description, permission, argumentAnnotations));
                    slashCommandExecutorMap.put(commandName, executor);
                }

                //Register the command and its aliases
                for (Guild guild : guilds) {
                    guild.updateCommands()
                            .addCommands(commandDataList)
                            .queue();
                }
                this.commandDataList.addAll(commandDataList);
            }
        }
    }

    private SlashCommandData generateSlashCommandData(String commandName, String description, Permission permission, SlashCommandArgument[] arguments) {
        SlashCommandData commandData = Commands.slash(commandName, description);
        for (SlashCommandArgument argument : arguments) {
            OptionData optionData = new OptionData(argument.type(), argument.name(), argument.description(), argument.required(), argument.autoComplete());
            for (SlashCommandChoice choice : argument.choices()) {
                optionData.addChoice(choice.name(), choice.value());
            }
            commandData.addOptions(optionData);
        }
        return commandData;
    }

    private boolean commandExists(String commandName) {
        return slashCommandExecutorMap.containsKey(commandName);
    }
}
