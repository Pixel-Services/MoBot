package com.pixelservices.api.env;

import com.pixelservices.MoBot;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

/**
 * Provides a basic bot environment with access to the {@link DefaultShardManagerBuilder}
 * and the instance of {@link MoBot}.
 * <p>
 * This class is used to encapsulate the bot's environment during initialization and module loading,
 * providing access to the shard manager builder and the bot instance.
 * </p>
 */
public class PrimitiveBotEnvironment {
    private final DefaultShardManagerBuilder builder;
    private final MoBot moBot;

    /**
     * Constructs a new {@link PrimitiveBotEnvironment} with the specified builder and bot instance.
     *
     * @param builder the {@link DefaultShardManagerBuilder} used to configure the shard manager
     * @param moBot the instance of {@link MoBot} representing the bot
     */
    public PrimitiveBotEnvironment(DefaultShardManagerBuilder builder, MoBot moBot) {
        this.builder = builder;
        this.moBot = moBot;
    }

    /**
     * Returns the {@link DefaultShardManagerBuilder} used to configure the shard manager.
     *
     * @return the {@link DefaultShardManagerBuilder}
     */
    public DefaultShardManagerBuilder getBuilder() {
        return builder;
    }

    /**
     * Returns the instance of {@link MoBot} representing the bot.
     *
     * @return the {@link MoBot} instance
     */
    public MoBot getInstance() {
        return moBot;
    }
}
