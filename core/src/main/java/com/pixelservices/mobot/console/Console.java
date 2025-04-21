package com.pixelservices.mobot.console;

import com.pixelservices.mobot.MoBot;
import com.pixelservices.mobot.console.impl.ModuleCommand;
import com.pixelservices.mobot.console.impl.SetTokenCommand;
import com.pixelservices.mobot.console.impl.VersionCommand;
import com.pixelservices.logger.Logger;
import com.pixelservices.logger.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Console {
    private final Map<String, ConsoleCommand> commands = new HashMap<>();
    private final ConsoleUtil consoleUtil;
    private final Scanner scanner;
    private final Logger logger;
    private final MoBot moBot;
    private final CountDownLatch latch;

    public Console(MoBot moBot, CountDownLatch latch) {
        this.logger = LoggerFactory.getLogger("Console");
        this.consoleUtil = new ConsoleUtil();
        this.scanner = new Scanner(System.in);
        this.moBot = moBot;
        this.latch = latch;
        clearConsole();
        new Thread(this::listenForCommands).start();
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

    public void clearConsole() {
        consoleUtil.clearConsole();
    }

    public void registerDefaults() {
        registerCommand("help", (args, logger) -> {
            logger.info("Available commands:");
            for (String command : commands.keySet()) {
                logger.info(" - " + command);
            }
        });
        registerCommand("clear", (args, logger) -> consoleUtil.clearConsole());
        registerCommand("shutdown", (args, logger) -> System.exit(0));
        registerCommand("stop", (args, logger) -> System.exit(0));
        registerCommand("settoken", new SetTokenCommand());
        registerCommand("modules" , (args, logger) -> {
            logger.info("Did you mean 'module list'?");
        });
        registerCommand("module", new ModuleCommand(moBot.getTaskScheduler(), moBot.getModuleManager()));
        registerCommand("version", new VersionCommand());
        logger.info("Registered " + commands.size() + " CLI-commands");
    }

    private void listenForCommands() {
        try {
            // Wait for the bot environment to be ready
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Command listener interrupted while waiting for bot environment.", e);
            return;
        }

        while (moBot.getFinalizedBotEnvironment() != null) {
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            String commandName = parts[0];
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);
            dispatchCommand(commandName, args);
        }
    }
}