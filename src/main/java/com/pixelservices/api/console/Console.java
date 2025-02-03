package com.pixelservices.api.console;

import com.pixelservices.MoBot;
import com.pixelservices.config.YamlConfig;
import com.pixelservices.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Console {
    private final Map<String, ConsoleCommand> commands = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private final Logger logger;

    public Console(MoBot moBot) {
        new Thread(this::listenForCommands).start();
        this.logger = moBot.getLogger();
        registerDefaults();
        logger.info("Registered " + commands.size() + " CLI-commands");
    }

    private void listenForCommands() {
        while (true) {
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            String commandName = parts[0];
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);
            dispatchCommand(commandName, args);
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
            logger.warn("Unknown command: " + name);
        }
    }

    private void registerDefaults() {
        registerCommand("help", args -> {
            logger.info("Available commands:");
            for (String command : commands.keySet()) {
                logger.info(" - " + command);
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
            YamlConfig yamlConfig = new YamlConfig("./bot.yml");
            yamlConfig.set("token", token);
            yamlConfig.save();
            logger.info("Token set to: " + token);
        });
    }
}