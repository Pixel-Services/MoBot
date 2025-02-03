package com.pixelservices.api.console;

import com.pixelservices.MoBot;
import com.pixelservices.api.config.ConfigLoader;
import org.simpleyaml.configuration.ConfigurationSection;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Console {
    private final Map<String, ConsoleCommand> commands = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private final MoBot moBot;
    private final Logger logger;

    public Console(MoBot moBot) {
        new Thread(this::listenForCommands).start();
        this.moBot = moBot;
        this.logger = moBot.getLogger();
        registerDefaults();
        logger.info("Registered {} CLI-commands", commands.size());
    }

    private void listenForCommands() {
        while (true) {
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            String commandName = parts[0];
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);

            ConsoleCommand command = commands.get(commandName);
            if (command != null) {
                command.execute(args);
            } else {
                logger.warn("Unknown command: {}", commandName);
            }
        }
    }

    public void registerCommand(String name, ConsoleCommand command) {
        commands.put(name, command);
    }

    public void dispatchCommand(String name, String[] args) {
        ConsoleCommand command = commands.get(name);
        if (command != null) {
            command.execute(args);
        } else {
            logger.warn("Unknown command: {}", name);
        }
    }

    private void registerDefaults() {
        registerCommand("help", args -> {
            logger.info("Available commands:");
            for (String command : commands.keySet()) {
                logger.info(" - {}", command);
            }
        });
        registerCommand("clear", args -> ConsoleUtil.clearConsole());
        registerCommand("shutdown", args -> System.exit(0));
        registerCommand("stop", args -> System.exit(0));

        registerCommand("settoken", args -> {
            if (args.length == 0) {
                logger.warn("No token provided.");
                return;
            }
            String token = args[0];
            ConfigLoader configLoader = new ConfigLoader("./bot.yml");
            ConfigurationSection config = configLoader.getConfig();
            config.set("token", token);
            configLoader.save();
            logger.info("Token set to: {}", token);
        });
    }
}