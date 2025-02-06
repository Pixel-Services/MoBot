package com.pixelservices.console;

import com.pixelservices.MoBot;
import com.pixelservices.console.impl.ModulesCommand;
import com.pixelservices.console.impl.SetTokenCommand;
import com.pixelservices.console.impl.VersionCommand;
import com.pixelservices.logger.Logger;
import com.pixelservices.logger.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Console {
    private final Map<String, ConsoleCommand> commands = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private final Logger logger;
    private final MoBot moBot;

    public Console(MoBot moBot) {
        new Thread(this::listenForCommands).start();
        this.logger = LoggerFactory.getLogger("Console");
        this.moBot = moBot;
        registerDefaults();
        logger.info("Registered " + commands.size() + " CLI-commands");
    }

    public void registerCommand(String name, ConsoleCommand command) {
        commands.put(name, command);
    }

    public void dispatchCommand(String name, String[] args) {
        ConsoleCommand command = commands.get(name);
        if (command != null) {
            try {
                command.execute(args, logger);
            } catch (Exception e) {
                logger.error("An error occurred while executing command: " + name, e);
            }
        } else {
            logger.warn("Unknown command: " + name);
        }
    }

    private void registerDefaults() {
        registerCommand("help", (args, logger) -> {
            logger.info("Available commands:");
            for (String command : commands.keySet()) {
                logger.info(" - " + command);
            }
        });
        registerCommand("clear", (args, logger) -> ConsoleUtil.clearConsole());
        registerCommand("shutdown", (args, logger) -> System.exit(0));
        registerCommand("stop", (args, logger) -> System.exit(0));
        registerCommand("settoken", new SetTokenCommand());
        registerCommand("modules", new ModulesCommand(moBot.getModuleManager()));
        registerCommand("version", new VersionCommand());
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
}