package com.pixelservices.modules;

import com.pixelservices.api.env.BotEnvironment;
import com.pixelservices.api.env.PrimitiveBotEnvironment;
import com.pixelservices.api.addons.SlashCommandAddon;
import com.pixelservices.api.config.ConfigLoader;
import com.pixelservices.plugin.Plugin;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public PrimitiveBotEnvironment getBotEnvironment() {
        return botEnvironment;
    }

    /**
     * Registers a slash command with the bot's command manager.
     *
     * @param data the {@link CommandData} for the slash command
     * @param addon the {@link SlashCommandAddon} to handle the slash command
     */
    public void registerSlashCommand(CommandData data, SlashCommandAddon addon){
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
    public void registerEventListener(Object... listeners){
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
     * Returns the {@link ConfigLoader} for this module.
     *
     * @return the {@link ConfigLoader} instance
     */
    public ConfigLoader getConfigLoader() {
        return generateConfig("config.yml");
    }

    /**
     * Returns a {@link ConfigLoader} instance for the specified resource file.
     *
     * @param resourceName the name of the resource file (e.g., "options.yml").
     * @return a {@link ConfigLoader} instance for the specified resource file
     */
    public ConfigLoader getConfigLoader(String resourceName) {
        return generateConfig(resourceName);
    }

    /**
     * Generates a configuration file for the module.
     *
     * @return a {@link ConfigLoader} instance of the generated configuration file
     */
    private ConfigLoader generateConfig(String resourceName) {
        try {
            Path configDir = Paths.get("modules" + "/" + getMetaData().getPluginId());
            Files.createDirectories(configDir);
            return new ConfigLoader(this.getClass(), resourceName, configDir);
        } catch (Exception e) {
            getLogger().error("Failed to generate configuration", e);
        }
        return null;
    }

    /**
     * Saves the default configuration file for the module.
     */
    private void saveDefaultConfig() {
        ConfigLoader configLoader = getConfigLoader();
        if (configLoader != null) {
            configLoader.save();
        } else {
            getLogger().error("Failed to save default configuration: No configuration file found.");
        }
    }
}
