package com.pixelservices.api;

import com.pixelservices.MoBot;
import com.pixelservices.manager.CommandManager;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * The {@code BotEnvironment} class encapsulates the core components required to operate
 * a bot within the MoBot system.
 * This class holds references to the {@link ShardManager},
 * the {@link MoBot} instance, and the {@link CommandManager}, which together provide the
 * essential environment for managing and executing bot commands.
 * <p>
 * The {@code BotEnvironment} class is immutable, meaning that the components it contains
 * cannot be changed once the object is created. This design ensures that the bot's environment
 * remains consistent throughout its lifecycle.
 * </p>
 *
 */
public class BotEnvironment extends PrimitiveBotEnvironment {
    private final ShardManager shardManager;
    private final CommandManager commandManager;

    /**
     * Constructs a new {@code BotEnvironment} object with the specified {@link ShardManager},
     * {@link MoBot} instance, and {@link CommandManager}.
     *
     * @param shardManager    the {@link ShardManager} responsible for managing bot shards
     * @param commandManager  the {@link CommandManager} responsible for handling commands
     */
    public BotEnvironment(ShardManager shardManager, MoBot moBot, CommandManager commandManager) {
        super(null, moBot);
        this.shardManager = shardManager;
        this.commandManager = commandManager;
    }

    /**
     * Returns the {@link ShardManager} responsible for managing bot shards.
     *
     * @return the {@link ShardManager}
     */
    public ShardManager getShardManager() {
        return shardManager;
    }

    /**
     * Returns the {@link CommandManager} responsible for handling commands.
     *
     * @return the {@link CommandManager}
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Returns the {@link DefaultShardManagerBuilder} used to configure the shard manager.
     *
     * @return the {@link DefaultShardManagerBuilder}
     */
    @Override
    public DefaultShardManagerBuilder getBuilder() {
        throw new UnsupportedOperationException("BotEnvironment does not support access to the ShardManagerBuilder");
    }
}
