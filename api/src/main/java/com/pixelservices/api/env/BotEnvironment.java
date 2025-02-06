package com.pixelservices.api.env;

import com.pixelservices.api.manager.CommandManager;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public interface BotEnvironment {
    CommandManager getCommandManager();
    DefaultShardManagerBuilder getBuilder();
    ShardManager getShardManager();
}
