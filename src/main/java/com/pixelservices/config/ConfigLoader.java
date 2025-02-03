package com.pixelservices.config;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * The ConfigLoader class provides utility methods for loading, saving, and managing YAML configuration files.
 */
public class ConfigLoader {
    private final File file;
    private final FileConfiguration config;

    /**
     * Constructs a ConfigLoader instance with the specified file path.
     *
     * @param path the path to the configuration file.
     */
    public ConfigLoader(String path) {
        this.file = new File(path);
        try {
            if (!this.file.exists()) {
                InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(this.file.getName());
                if (resourceStream != null) {
                    Files.copy(resourceStream, this.file.toPath());
                } else {
                    this.file.createNewFile();
                }
            }
            this.config = YamlConfiguration.loadConfiguration(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the configuration to the file.
     */
    public void save() {
        try {
            this.config.save(this.file);
        } catch (Exception e) {
           System.out.println("Failed to save configuration file." + e);
        }
    }

    /**
     * Gets the FileConfiguration object.
     *
     * @return the FileConfiguration object.
     */
    public FileConfiguration getConfig() {
        return this.config;
    }
}
