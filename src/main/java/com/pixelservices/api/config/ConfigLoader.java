package com.pixelservices.api.config;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The ConfigLoader class provides utility methods for loading, saving, and managing YAML configuration files.
 */
public class ConfigLoader {
    private final File file;
    private final FileConfiguration config;

    /**
     * Constructs a ConfigLoader instance that loads a configuration file from within a JAR.
     *
     * @param moduleClass the class from the module JAR, used to load resources.
     * @param resourceName the name of the resource file (e.g., "module.yml").
     */
    public ConfigLoader(Class<?> moduleClass, String resourceName) {
        this.file = null; // No file associated when loading from a resource within the JAR
        try (InputStream resourceStream = moduleClass.getClassLoader().getResourceAsStream(resourceName)) {
            if (resourceStream == null) {
                throw new IOException("Resource " + resourceName + " not found in the JAR.");
            }
            this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(resourceStream));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource " + resourceName + " from the JAR.", e);
        }
    }

    /**
     * Constructs a ConfigLoader instance that loads a configuration file from within a JAR and saves it to a specified path.
     *
     * @param moduleClass the class from the module JAR, used to load resources.
     * @param resourceName the name of the resource file (e.g., "module.yml").
     * @param path the path to save the configuration file.
     */
    public ConfigLoader(Class<?> moduleClass, String resourceName, Path path) {
        this.file = new File(path + "/" + resourceName);

        try {
            if (!this.file.exists()) {
                InputStream resourceStream = moduleClass.getClassLoader().getResourceAsStream(this.file.getName());
                if (resourceStream != null) {
                    Files.copy(resourceStream, this.file.toPath());
                } else {
                    this.file.createNewFile();
                }
            }
            this.config = YamlConfiguration.loadConfiguration(this.file);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
     * Constructs a ConfigLoader instance with the specified InputStream.
     *
     * @param inputStream the InputStream to the configuration file.
     */
    public ConfigLoader(InputStream inputStream) {
        try {
            this.file = null; // No file associated when loading from an InputStream
            this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
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
     * Gets the configuration file.
     *
     * @return the configuration file.
     */
    public File getFile() {
        return this.file;
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
