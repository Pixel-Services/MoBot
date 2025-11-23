package com.pixelservices.mobot;

import com.pixelservices.mobot.api.env.FinalizedBotEnvironment;
import com.pixelservices.mobot.api.env.PrimitiveBotEnvironment;
import com.pixelservices.mobot.commands.CommandManager;
import com.pixelservices.mobot.console.Console;
import com.pixelservices.mobot.exceptions.BotStartupException;
import com.pixelservices.mobot.modules.ModuleManager;
import com.pixelservices.mobot.scheduler.BotTaskScheduler;
import com.pixelservices.mobot.utils.ConfigUtil;
import com.pixelservices.mobot.utils.UpdateChecker;
import dev.siea.jonion.configuration.YamlPluginConfig;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * The main class for initializing and managing the MoBot application.
 * <p>
 * This class handles the initialization of the bot, loading of modules,
 * setting up the bot environment, and managing the lifecycle of the bot.
 * </p>
 */
public class MoBot {

    private final Logger logger;
    private final BotTaskScheduler taskScheduler;
    private final ModuleManager moduleManager;
    private final Console console;
    private FinalizedBotEnvironment finalizedBotEnvironment;

    public MoBot(String[] args) {
        Instant startTime = Instant.now();

        // Initialize the Logger
        logger = LoggerFactory.getLogger("MoBot");

        // Initialize the Console
        CountDownLatch consoleLatch = new CountDownLatch(1);
        console = new Console(this, consoleLatch);

        // Generate the DefaultShardManagerBuilder without initializing it
        DefaultShardManagerBuilder builder = getBuilder();

        // Set up the PrimitiveBotEnvironment and pass in all data available pre enabling
        PrimitiveBotEnvironment primitiveBotEnvironment = new PrimitiveBotEnvironment(builder);

        // Initialize the CommandManager
        CommandManager commandManager = new CommandManager();

        // Initialize the TaskScheduler
        taskScheduler = new BotTaskScheduler();

        // Initialize the ModuleManager
        moduleManager = new ModuleManager(taskScheduler, commandManager);

        // Pre-enable the modules
        moduleManager.preEnable(primitiveBotEnvironment);

        // Start the bot and construct the ShardManager
        ShardManager shardManager;
        try {
            shardManager = enableBot(builder);
            logger.info("Successfully enabled shard manager with {} shards.", shardManager.getShardsTotal());
        } catch (BotStartupException e) {
            logger.error("Bot startup failed", e);
            return;
        }

        // Set up the BotEnvironment
        finalizedBotEnvironment = new FinalizedBotEnvironment(shardManager);

        consoleLatch.countDown();

        // Register the CommandManager
        shardManager.addEventListener(commandManager);

        //Enable the modules
        moduleManager.enable(finalizedBotEnvironment);

        // Register the default commands
        console.registerDefaults();

        // Log the startup time
        logger.info("MoBot startup completed in {} seconds.", Duration.between(startTime, Instant.now()).toSeconds());

        // Check for updates
        YamlPluginConfig yamlConfig = ConfigUtil.getBotConfig();
        if (yamlConfig.getBoolean("check-updates")) {
            UpdateChecker updateChecker = new UpdateChecker();
            if (updateChecker.isLatest()) {
                logger.info("You are using the latest version of MoBot: {}.", updateChecker.getCurrentVersion());
            } else {
                logger.info("A new version of MoBot is available: {}. You are currently on version {}.", updateChecker.getLatestVersion(), updateChecker.getCurrentVersion());
            }
        }
    }

    public static void main(String[] args) {
        MoBot bot = new MoBot(args);
        Runtime.getRuntime().addShutdownHook(new Thread(bot::shutdown));
    }

    public void shutdown() {
        logger.info("Shutting down MoBot...");

        taskScheduler.shutdown();
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

    public BotTaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public FinalizedBotEnvironment getFinalizedBotEnvironment() {
        return finalizedBotEnvironment;
    }

    private DefaultShardManagerBuilder getBuilder() {
        YamlPluginConfig yamlConfig = ConfigUtil.getBotConfig();
        yamlConfig.save();
        String token = yamlConfig.getString("token");
        List<String> gateWayIntents = yamlConfig.getYamlConfiguration().getStringList("gateway-intents");
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        for (String intent : gateWayIntents) {
            builder.enableIntents(GatewayIntent.valueOf(intent));
        }
        return builder;
    }

    private ShardManager enableBot(DefaultShardManagerBuilder builder) throws BotStartupException {
        ShardManager shardManager = null;
        Scanner scanner = new Scanner(System.in);

        YamlPluginConfig yamlConfig = ConfigUtil.getBotConfig();

        String token = yamlConfig.getString("token");

        if(token == null || token.isBlank()) {
            logger.info("No Discord Bot-Token found. This might be your first time running the bot. Please enter a valid bot token: ");
        }

        while (true) {
            if(token != null && !token.isBlank()) {
                try {
                    shardManager = builder.build();
                    return shardManager;
                } catch (InvalidTokenException e) {
                    logger.info("The configured Discord Bot-Token is now invalid. Please enter a new token: ");
                } catch (Exception e) {
                    throw new BotStartupException("An unknown error occurred while setting up the shard manager.", e);
                }
            }

            token = scanner.nextLine();

            if (token.isBlank()) {
                logger.warn("The provided Discord Bot-Token cannot be empty. Please enter a new token: ");
                continue;
            }

            yamlConfig.set("token", token);
            yamlConfig.save();
            builder.setToken(token);

            try {
                shardManager = builder.build();
                return shardManager;
            } catch (InvalidTokenException e) {
                logger.info("The provided Discord Bot-Token is invalid. Please enter a new token: ");
                token = null;
            } catch (Exception e) {
                throw new BotStartupException("An unknown error occurred while setting up the shard manager.", e);
            }
        }
    }

    private boolean containsArg(String[] args, String target) {
        return Arrays.stream(args).toList().contains(target);
    }
}