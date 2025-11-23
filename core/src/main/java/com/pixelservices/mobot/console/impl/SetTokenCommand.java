package com.pixelservices.mobot.console.impl;

import com.pixelservices.mobot.console.ConsoleCommand;
import com.pixelservices.mobot.utils.ConfigUtil;
import dev.siea.jonion.configuration.YamlPluginConfig;
import org.slf4j.Logger;

public class SetTokenCommand implements ConsoleCommand {

    @Override
    public void execute(String[] args, Logger logger) {
        if (args.length == 0) {
            logger.warn("No token provided.");
            return;
        }
        String token = args[0];
        YamlPluginConfig yamlConfig = ConfigUtil.getBotConfig();
        yamlConfig.set("token", token);
        yamlConfig.save();
        logger.info("Token set to: {}", token);
    }
}
