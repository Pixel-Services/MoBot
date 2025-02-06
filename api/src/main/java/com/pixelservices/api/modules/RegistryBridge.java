package com.pixelservices.api.modules;

import com.pixelservices.api.addons.SlashCommandAddon;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface RegistryBridge {
    void registerCommand(CommandData commandData, SlashCommandAddon slashCommandAddon);
}
