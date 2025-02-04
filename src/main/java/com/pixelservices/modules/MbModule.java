package com.pixelservices.modules;

import com.pixelservices.api.env.BotEnvironment;
import com.pixelservices.api.env.PrimitiveBotEnvironment;
import com.pixelservices.api.addons.SlashCommandAddon;
import com.pixelservices.plugin.Plugin;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class MbModule extends Plugin {
    private PrimitiveBotEnvironment botEnvironment;

    /**
     * Called before the bot is enabled.
     * <p>
     * This method is intended to be overridden by subclasses to perform any necessary initialization
     * before the bot is enabled.
     * </p>
     *
     */
    public void preEnable() {

    }

    /**
     * Called when the bot is enabled.
     * <p>
     * This method is intended to be overridden by subclasses to perform any necessary initialization
     * when the bot is enabled.
     * </p>
     *
     */
    public void onEnable() {

    }

    /**
     * Called before the bot is disabled.
     * <p>
     * This method is intended to be overridden by subclasses to perform any necessary cleanup
     * before the bot is disabled.
     * </p>
     *
     */
    public void preDisable() {

    }

    /**
     * Called when the bot is disabled.
     * <p>
     * This method is intended to be overridden by subclasses to perform any necessary cleanup
     * when the bot is disabled.
     * </p>
     *
     */
    public void onDisable() {

    }

    /**
     * Returns the {@link BotEnvironment}.
     *
     * @return the {@link BotEnvironment} instance
     */
    public final PrimitiveBotEnvironment getBotEnvironment() {
        return botEnvironment;
    }

    /**
     * Registers a slash command with the bot's command manager.
     *
     * @param data the {@link CommandData} for the slash command
     * @param addon the {@link SlashCommandAddon} to handle the slash command
     */
    public final void registerSlashCommand(CommandData data, SlashCommandAddon addon){
        if (botEnvironment instanceof BotEnvironment botEnv) {
            botEnv.getCommandManager().registerCommand(data, addon);
        } else {
            getLogger().error("Failed to register slash command: ShardManager is not available yet. Please register commands after the onEnable method was called.");
        }
    }

    /**
     * Registers event listeners with the bot's shard manager.
     *
     * @param listeners the event listeners to be registered
     */
    public final void registerEventListener(Object... listeners){
        if (botEnvironment instanceof BotEnvironment botEnv) {
            botEnv.getShardManager().addEventListener(listeners);
        } else {
            getLogger().error("Failed to register event listeners: ShardManager is not available yet. Please register listeners after the onEnable method was called.");
        }
    }

    /**
     * Injects the {@link PrimitiveBotEnvironment} into the module.
     *
     * @param botEnvironment the {@link PrimitiveBotEnvironment} to inject
     */
    final void injectPrimitiveBotEnvironment(PrimitiveBotEnvironment botEnvironment) {
        this.botEnvironment = botEnvironment;
    }

    /**
     * Injects the {@link BotEnvironment} into the module.
     *
     * @param botEnvironment the {@link BotEnvironment} to inject
     */
    final void injectBotEnvironment(BotEnvironment botEnvironment) {
        this.botEnvironment = botEnvironment;
    }

    /**
     * Saves the default configuration file for the module.
     */
    protected final void saveDefaultConfig() {
        getDefaultConfig().save();
    }
}
