package com.pixelservices.mobot.api.env;

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * Provides a basic bot environment with access to the {@link DefaultShardManagerBuilder}
 * <p>
 * This class is used to encapsulate the bot's environment during initialization and module loading,
 * providing access to the shard manager builder and the bot instance.
 * </p>
 */
public class PrimitiveBotEnvironment implements BotEnvironment {
    private final DefaultShardManagerBuilder builder;

    /**
     * Constructs a new {@link PrimitiveBotEnvironment} with the specified builder and bot instance.
     *
     * @param builder the {@link DefaultShardManagerBuilder} used to configure the shard manager
     */
    public PrimitiveBotEnvironment(DefaultShardManagerBuilder builder) {
        this.builder = builder;
    }

    /**
     * Returns the {@link DefaultShardManagerBuilder} used to configure the shard manager.
     *
     * @return the {@link DefaultShardManagerBuilder}
     */
    @Override
    public DefaultShardManagerBuilder getBuilder() {
        return builder;
    }

    @Override
    public ShardManager getShardManager() {
        throw new UnsupportedOperationException("The BotEnvironment has not yet been finalized and does not support access to the ShardManager");
    }
}
