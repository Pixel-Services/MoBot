package com.pixelservices.mobot.utils;

import dev.siea.jonion.configuration.YamlPluginConfig;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigUtil {
    private final static Logger logger = LoggerFactory.getLogger("MoBot");

    public static YamlPluginConfig getBotConfig() {
        String fileName = "bot.yml";
        Path path = Paths.get(fileName);
        File file = path.toFile();

        YamlConfiguration yamlConfig = new YamlConfiguration();

        if (Files.exists(path)) {
            try {
                yamlConfig.load(file);
            } catch (IOException e) {
                logger.error("Failed to load bot.yml", e);
                return null;
            }
        } else {
            // Try to load from resources if file doesn't exist
            try (InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream(fileName)) {
                if (inputStream != null) {
                    yamlConfig.load(inputStream);
                } else {
                    yamlConfig.loadFromString("");
                }
            } catch (IOException e) {
                logger.error("Failed to load bot.yml from resources", e);
                return null;
            }
        }

        return new YamlPluginConfig(yamlConfig, file.getAbsoluteFile().toPath());
    }
}
