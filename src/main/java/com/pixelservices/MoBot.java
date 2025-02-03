package com.pixelservices;

import com.pixelservices.logger.Logger;
import com.pixelservices.logger.LoggerFactory;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import com.pixelservices.api.env.BotEnvironment;
import com.pixelservices.api.env.PrimitiveBotEnvironment;
import com.pixelservices.config.ConfigLoader;
import com.pixelservices.api.console.Console;
import com.pixelservices.exceptions.BotStartupException;
import com.pixelservices.manager.CommandManager;
import com.pixelservices.api.console.ConsoleUtil;
import com.pixelservices.modules.ModuleManager;
import org.simpleyaml.configuration.ConfigurationSection;

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
    private BotEnvironment botEnvironment;
    private final Logger logger;
    private final ModuleManager moduleManager;
    private Console console;

    public MoBot(String[] args) {
        Instant startTime = Instant.now();

        ConsoleUtil.clearConsole();

        // Initialize the Logger
        logger = LoggerFactory.getLogger("MoBot");

        // Generate the DefaultShardManagerBuilder without initializing it
        DefaultShardManagerBuilder builder = getBuilder();

        // Set up the PrimitiveBotEnvironment and pass in all data available pre enabling
        PrimitiveBotEnvironment primitiveBotEnvironment = new PrimitiveBotEnvironment(builder, this);

        // Initialize the ModuleSystem
        moduleManager = new ModuleManager();

        // Pre-enable the modules
        moduleManager.preEnable(primitiveBotEnvironment);

        // Start the bot and construct the ShardManager
        ShardManager shardManager;
        try {
            shardManager = enableBot(builder);
            logger.info("Successfully enabled shard manager with " + shardManager.getShardsTotal() +  " shards.");
        } catch (BotStartupException e) {
            logger.error("Bot startup failed", e);
            return;
        }

        // Initialize the CommandManager
        CommandManager commandManager = new CommandManager();

        // Set up the BotEnvironment
        botEnvironment = new BotEnvironment(shardManager, this, commandManager);

        // Register the CommandManager
        shardManager.addEventListener(commandManager);

        //Enable the modules
        moduleManager.enable(botEnvironment);

        // Initialize the Console
        console = new Console(this);

        logger.info("MoBot startup completed in " + Duration.between(startTime, Instant.now()).toSeconds() + " seconds.");
    }

    private DefaultShardManagerBuilder getBuilder() {
        ConfigLoader configLoader = new ConfigLoader("./bot.yml");
        configLoader.save();
        ConfigurationSection config = configLoader.getConfig();
        String token = config.getString("token");
        List<String> gateWayIntents = config.getStringList("gateway-intents");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);

        for (String intent : gateWayIntents) {
            builder.enableIntents(GatewayIntent.valueOf(intent));
        }

        return builder;
    }

    private ShardManager enableBot(DefaultShardManagerBuilder builder) throws BotStartupException {
        ShardManager shardManager = null;
        Scanner scanner = new Scanner(System.in);

        ConfigLoader configLoader = new ConfigLoader("./bot.yml");
        ConfigurationSection config = configLoader.getConfig();
        String token = config.getString("token");

        if (token == null || token.isEmpty()) {
            ConsoleUtil.print("No Discord Bot-Token found. This might be your first time running the bot. Please enter a valid bot token: ");
            token = scanner.nextLine();
            config.set("token", token);
            configLoader.save();
            builder.setToken(token);
        }

        while (shardManager == null) {
            try {
                shardManager = builder.build();
            } catch (InvalidTokenException e) {
                logger.info("The provided Discord Bot-Token is invalid. Please enter a new token: ");
                String newToken = scanner.nextLine();
                config.set("token", newToken);
                configLoader.save();
                builder.setToken(newToken);
            } catch (Exception e) {
                throw new BotStartupException("An unknown error occurred while setting up the shard manager.", e);
            }
        }

        return shardManager;
    }

    public void shutdown() {
        logger.info("Shutting down MoBot...");

        moduleManager.preDisable();

        if (botEnvironment != null && botEnvironment.getShardManager() != null) {
            botEnvironment.getShardManager().shutdown();
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

    public static void main(String[] args) {
        MoBot bot = new MoBot(args);
        Runtime.getRuntime().addShutdownHook(new Thread(bot::shutdown));
    }

    private boolean containsArg(String[] args, String target) {
        return Arrays.stream(args).toList().contains(target);
    }
}