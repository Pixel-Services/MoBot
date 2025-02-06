package com.pixelservices.api.env;

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public interface BotEnvironment {
    DefaultShardManagerBuilder getBuilder();
    ShardManager getShardManager();
}
