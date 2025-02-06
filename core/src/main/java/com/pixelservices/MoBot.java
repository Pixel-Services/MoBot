package com.pixelservices;

import com.pixelservices.config.YamlConfig;
import com.pixelservices.logger.Logger;
import com.pixelservices.logger.LoggerFactory;
import com.pixelservices.utils.UpdateChecker;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import com.pixelservices.api.env.FinalizedBotEnvironment;
import com.pixelservices.api.env.PrimitiveBotEnvironment;
import com.pixelservices.console.Console;
import com.pixelservices.exceptions.BotStartupException;
import com.pixelservices.commands.CommandManager;
import com.pixelservices.modules.ModuleManager;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * The main class for initializing and managing the MoBot application.
 * <p>
 * This class handles the initialization of the bot, loading of modules,
 * setting up the bot environment, and managing the lifecycle of the bot.
 * </p>
 */
public class MoBot {
    private FinalizedBotEnvironment finalizedBotEnvironment;
    private final Logger logger;
    private final ModuleManager moduleManager;
    private final Console console;

    public MoBot(String[] args) {
        Instant startTime = Instant.now();

        // Initialize the Logger
        logger = LoggerFactory.getLogger("MoBot");

        // Initialize the Console
        console = new Console(this);

        // Generate the DefaultShardManagerBuilder without initializing it
        DefaultShardManagerBuilder builder = getBuilder();

        // Set up the PrimitiveBotEnvironment and pass in all data available pre enabling
        PrimitiveBotEnvironment primitiveBotEnvironment = new PrimitiveBotEnvironment(builder);

        // Initialize the ModuleManager
        moduleManager = new ModuleManager();

        // Initialize the CommandManager
        CommandManager commandManager = new CommandManager();

        // Pre-enable the modules
        moduleManager.preEnable(primitiveBotEnvironment, commandManager);

        // Start the bot and construct the ShardManager
        ShardManager shardManager;
        try {
            shardManager = enableBot(builder);
            logger.info("Successfully enabled shard manager with " + shardManager.getShardsTotal() +  " shards.");
        } catch (BotStartupException e) {
            logger.error("Bot startup failed", e);
            return;
        }

        // Set up the BotEnvironment
        finalizedBotEnvironment = new FinalizedBotEnvironment(shardManager);

        // Register the CommandManager
        shardManager.addEventListener(commandManager);

        //Enable the modules
        moduleManager.enable(finalizedBotEnvironment);

        // Register the default commands
        console.registerDefaults();

        // Log the startup time
        logger.info("MoBot startup completed in " + Duration.between(startTime, Instant.now()).toSeconds() + " seconds.");

        // Check for updates
        YamlConfig yamlConfig = new YamlConfig("bot.yml");
        if (yamlConfig.getBoolean("check-updates")) {
            UpdateChecker updateChecker = new UpdateChecker();
            if (updateChecker.isLatest()) {
                logger.info("You are using the latest version of MoBot: " + updateChecker.getCurrentVersion() + ".");
            } else {
                logger.info("A new version of MoBot is available: " + updateChecker.getLatestVersion() + ". You are currently on version " + updateChecker.getCurrentVersion() + ".");
            }
        }
    }

    public void shutdown() {
        logger.info("Shutting down MoBot...");

        moduleManager.preDisable();

        if (finalizedBotEnvironment != null && finalizedBotEnvironment.getShardManager() != null) {
            finalizedBotEnvironment.getShardManager().shutdown();
            logger.info("Shard manager has been shut down.");
        }

        moduleManager.disable();

        logger.info("See you soon!.");
    }

    public Logger getLogger() {
        return logger;
    }

    public Console getConsole() {
        return console;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static void main(String[] args) {
        MoBot bot = new MoBot(args);
        Runtime.getRuntime().addShutdownHook(new Thread(bot::shutdown));
    }

    private DefaultShardManagerBuilder getBuilder() {
        YamlConfig yamlConfig = new YamlConfig("bot.yml");
        yamlConfig.save();
        String token = yamlConfig.getString("token");
        List<String> gateWayIntents = yamlConfig.getStringList("gateway-intents");
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        for (String intent : gateWayIntents) {
            builder.enableIntents(GatewayIntent.valueOf(intent));
        }
        return builder;
    }

    private ShardManager enableBot(DefaultShardManagerBuilder builder) throws BotStartupException {
        ShardManager shardManager = null;
        Scanner scanner = new Scanner(System.in);

        YamlConfig yamlConfig = new YamlConfig("bot.yml");

        String token = yamlConfig.getString("token");

        if (token == null || token.isEmpty()) {
            logger.info("No Discord Bot-Token found. This might be your first time running the bot. Please enter a valid bot token: ");
            token = scanner.nextLine();
            yamlConfig.set("token", token);
            yamlConfig.save();
            builder.setToken(token);
        }

        while (shardManager == null) {
            try {
                shardManager = builder.build();
            } catch (InvalidTokenException e) {
                logger.info("The provided Discord Bot-Token is invalid. Please enter a new token: ");
                String newToken = scanner.nextLine();
                yamlConfig.set("token", newToken);
                yamlConfig.save();
                builder.setToken(newToken);
            } catch (Exception e) {
                throw new BotStartupException("An unknown error occurred while setting up the shard manager.", e);
            }
        }

        return shardManager;
    }

    private boolean containsArg(String[] args, String target) {
        return Arrays.stream(args).toList().contains(target);
    }
}