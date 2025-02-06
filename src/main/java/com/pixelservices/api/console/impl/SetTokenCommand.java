package com.pixelservices.api.console.impl;

import com.pixelservices.api.console.ConsoleCommand;
import com.pixelservices.config.YamlConfig;
import com.pixelservices.logger.Logger;

public class SetTokenCommand implements ConsoleCommand {

    @Override
    public void execute(String[] args, Logger logger) {
        if (args.length == 0) {
            logger.warn("No token provided.");
            return;
        }
        String token = args[0];
        YamlConfig yamlConfig = new YamlConfig("./bot.yml");
        yamlConfig.set("token", token);
        yamlConfig.save();
        logger.info("Token set to: " + token);
    }
}
