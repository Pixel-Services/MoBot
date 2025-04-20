package com.pixelservices.api.modules;

import com.pixelservices.api.addons.SlashCommandAddon;
import com.pixelservices.api.commands.SlashCommandHandler;
import com.pixelservices.api.env.BotEnvironment;
import com.pixelservices.api.env.FinalizedBotEnvironment;
import com.pixelservices.api.env.PrimitiveBotEnvironment;
import com.pixelservices.api.commands.SlashCommand;
import com.pixelservices.plugin.Plugin;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class MbModule extends Plugin {
    private BotEnvironment botEnvironment;
    private RegistryBridge registryBridge;

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
    public final BotEnvironment getBotEnvironment() {
        return botEnvironment;
    }

    /**
     * Registers a slash command with the bot.
     *
     * @param data the {@link CommandData} for the slash command containing the command name, description, and options for the command
     * @param addon the {@link SlashCommandAddon} to handle the command interaction
     */
    @Deprecated(forRemoval = true)
    public final void registerSlashCommand(CommandData data, SlashCommandAddon addon){
        registryBridge.registerCommand(data, addon);
    }

    /**
     * Registers a slash command handler with the bot.
     *
     * @param slashCommandHandler the {@link SlashCommand} to handle the command interaction
     */
    public final void registerSlashCommandHandler(SlashCommandHandler slashCommandHandler){
        registryBridge.registerCommandHandler(slashCommandHandler);
    }

    /**
     * Registers event listeners with the bot.
     *
     * @param listeners the event listeners to be registered
     */
    public final void registerEventListener(Object... listeners){
        if (botEnvironment instanceof FinalizedBotEnvironment botEnv) {
            botEnv.getShardManager().addEventListener(listeners);
        } else {
            getLogger().error("Failed to register event listeners: Bot is not available yet. Please register listeners after the onEnable method was called.");
        }
    }

    /**
     * Injects the {@link PrimitiveBotEnvironment} into the module.
     *
     * @param botEnvironment the {@link PrimitiveBotEnvironment} to inject
     */
    public final void inject(PrimitiveBotEnvironment botEnvironment, RegistryBridge registryBridge) {
        this.botEnvironment = botEnvironment;
        this.registryBridge = registryBridge;
    }

    /**
     * Injects the {@link FinalizedBotEnvironment} into the module.
     *
     * @param finalizedBotEnvironment the {@link FinalizedBotEnvironment} to inject
     */
    public final void finalizeBotEnvironment(FinalizedBotEnvironment finalizedBotEnvironment) {
        this.botEnvironment = finalizedBotEnvironment;
    }

    /**
     * Saves the default configuration file for the module.
     */
    protected final void saveDefaultConfig() {
        getDefaultConfig().save();
    }
}
