package com.pixelservices.console.impl;

import com.pixelservices.config.ConfigFactory;
import com.pixelservices.console.ConsoleCommand;
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
        YamlConfig yamlConfig = ConfigFactory.getYamlConfig("./bot.yml");
        yamlConfig.set("token", token);
        yamlConfig.save();
        logger.info("Token set to: " + token);
    }
}
