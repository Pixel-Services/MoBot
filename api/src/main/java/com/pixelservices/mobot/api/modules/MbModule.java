package com.pixelservices.mobot.api.modules;

import com.pixelservices.mobot.api.commands.SlashCommand;
import com.pixelservices.mobot.api.commands.SlashCommandHandler;
import com.pixelservices.mobot.api.env.BotEnvironment;
import com.pixelservices.mobot.api.env.FinalizedBotEnvironment;
import com.pixelservices.mobot.api.env.PrimitiveBotEnvironment;
import com.pixelservices.mobot.api.modules.listener.ListenerBridge;
import com.pixelservices.mobot.api.modules.listener.ModuleListener;
import com.pixelservices.mobot.api.scheduler.ScheduledTask;
import com.pixelservices.mobot.api.scheduler.TaskScheduler;
import com.pixelservices.plugin.Plugin;

public class MbModule extends Plugin {

    private TaskScheduler taskScheduler;
    private ModuleRegistry moduleRegistry;
    private BotEnvironment botEnvironment;
    private RegistryBridge registryBridge;
    private ListenerBridge listenerBridge;

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

    public ModuleRegistry getModuleManager() {
        return moduleRegistry;
    }

    /**
     * Returns the {@link BotEnvironment}.
     *
     * @return the {@link BotEnvironment} instance
     */
    public final BotEnvironment getBotEnvironment() {
        return botEnvironment;
    }

    public ListenerBridge getListenerBridge() {
        return listenerBridge;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
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
    public final void registerEventListener(ModuleListener... listeners){
        if (listenerBridge != null) {
            for (ModuleListener listener : listeners) {
                if(listenerBridge.getListeners().contains(listener)) {
                    getLogger().warn("Skipping listener: " + listener.getClass().getSimpleName() + ", as it's already registered.");
                    continue;
                }

                listenerBridge.registerListener(listener);
            }
        } else {
            getLogger().error("Failed to register event listeners: Bot is not available yet. Please register listeners after the onEnable method was called.");
        }
    }

    /**
     * Injects the {@link PrimitiveBotEnvironment} into the module.
     *
     * @param botEnvironment the {@link PrimitiveBotEnvironment} to inject
     */
    public final void inject(TaskScheduler taskScheduler, ModuleRegistry moduleRegistry, PrimitiveBotEnvironment botEnvironment, RegistryBridge registryBridge) {
        this.taskScheduler = taskScheduler;
        this.moduleRegistry = moduleRegistry;
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

    public final void listenerBridge(ListenerBridge listenerBridge) {
        this.listenerBridge = listenerBridge;
    }

    /**
     * Saves the default configuration file for the module.
     */
    protected final void saveDefaultConfig() {
        getDefaultConfig().save();
    }

    public final String getId() {
        return getPluginWrapper().getPluginDescriptor().getPluginId();
    }

}
