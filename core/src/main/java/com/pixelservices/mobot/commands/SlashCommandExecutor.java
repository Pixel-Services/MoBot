package com.pixelservices.mobot.commands;

import com.pixelservices.mobot.api.commands.CommandArguments;
import com.pixelservices.mobot.api.commands.SlashCommandArgument;
import com.pixelservices.mobot.api.commands.SlashCommandHandler;
import com.pixelservices.mobot.exceptions.CommandExecuteException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SlashCommandExecutor is responsible for executing a slash command when it is invoked.
 * It uses reflection to invoke the appropriate method in the handler with the provided arguments.
 */
class SlashCommandExecutor {
    private final SlashCommandHandler handler;
    private final Method method;
    private final List<SlashCommandArgument> arguments;

    /**
     * Constructor for SlashCommandExecutor.
     *
     * @param handler    The handler that contains the command method.
     * @param method     The method to be executed when the command is invoked.
     * @param arguments  The list of arguments for the command.
     */
    SlashCommandExecutor(SlashCommandHandler handler, Method method, List<SlashCommandArgument> arguments) {
        this.handler = handler;
        this.method = method;
        this.arguments = arguments;
    }

    /**
     * Executes the command with the provided event and arguments.
     *
     * @param event The SlashCommandInteractionEvent containing information about the command interaction.
     */
    void execute(SlashCommandInteractionEvent event) {
        try {
            Parameter[] parameters = method.getParameters();
            if (parameters.length == 0 || parameters[0].getType() != SlashCommandInteractionEvent.class || parameters.length > 2) {
                throw new CommandExecuteException("Incompatible method parameters. " + Arrays.toString(parameters));
            }

            if (parameters.length == 1) {
                method.invoke(handler, event);
                return;
            }

            Object argumentContainer;

            Class<?> paramType = parameters[1].getType();
            if (Map.class.isAssignableFrom(paramType)) {
                // OLD WAY (DEPRECATED)
                Map<String, Object> legacyArgs = new HashMap<>();
                for (SlashCommandArgument arg : arguments) {
                    legacyArgs.put(arg.name(), event.getOption(arg.name()));
                }
                argumentContainer = legacyArgs;
            } else if (paramType == CommandArguments.class) {
                CommandArguments parsedArgs = new CommandArguments();
                for (SlashCommandArgument arg : arguments) {
                    parsedArgs.put(arg.name(), event.getOption(arg.name()));
                }
                argumentContainer = parsedArgs;

            } else {
                throw new CommandExecuteException("Unsupported argument type: " + paramType.getName());
            }

            method.invoke(handler, event, argumentContainer);

        } catch (Exception e) {
            throw new CommandExecuteException(e);
        }
    }
}