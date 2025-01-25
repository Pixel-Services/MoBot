package net.vitacraft.modules;

import net.vitacraft.api.PrimitiveBotEnvironment;
import net.vitacraft.api.info.ModuleInfo;
import org.pf4j.*;
import org.simpleyaml.configuration.file.YamlFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ModuleManager extends DefaultPluginManager {

    public ModuleManager() {
        super(Paths.get("modules"));
    }

    @Override
    protected PluginLoader createPluginLoader() {
        return new JarPluginLoader(this);
    }

    @Override
    protected PluginWrapper loadPluginFromPath(Path pluginPath) {
        // Load module.yml
        YamlFile yamlFile = new YamlFile(pluginPath.resolve("module.yml").toFile());
        try {
            yamlFile.load();
        } catch (Exception e) {
            throw new PluginRuntimeException("Failed to load module.yml", e);
        }

        // Create ModuleInfo
        ModuleInfo moduleInfo = new ModuleInfo(
                yamlFile.getString("name"),
                yamlFile.getString("version"),
                yamlFile.getString("description"),
                yamlFile.getStringList("authors"),
                yamlFile.getStringList("dependencies"),
        );

        // Create PluginWrapper
        PluginClassLoader pluginClassLoader = new PluginClassLoader(this, pluginDescriptor, getClass().getClassLoader());
        PluginWrapper pluginWrapper = new PluginWrapper(this, pluginDescriptor, pluginPath, pluginClassLoader);

        // Ensure the plugin is an instance of MbModuleAdapter
        if (!(pluginWrapper.getPlugin() instanceof MbModuleAdapter)) {
            throw new PluginRuntimeException("Loaded plugin is not an instance of MbModuleAdapter");
        }

        return pluginWrapper;
    }

    public void preEnable(PrimitiveBotEnvironment environment) {
        for (PluginWrapper plugin : getPlugins()) {
            MbModuleAdapter module = (MbModuleAdapter) plugin.getPlugin();
            module.preEnable(environment);
        }
    }

    @Override
    public void startPlugins() {
        for (PluginWrapper plugin : getPlugins()) {
            MbModuleAdapter module = (MbModuleAdapter) plugin.getPlugin();
            module.start();
        }
    }

    public void preDisable() {
        for (PluginWrapper plugin : getPlugins()) {
            MbModuleAdapter module = (MbModuleAdapter) plugin.getPlugin();
            module.preDisable();
        }
    }

    @Override
    public void stopPlugins() {
        for (PluginWrapper plugin : getPlugins()) {
            MbModuleAdapter module = (MbModuleAdapter) plugin.getPlugin();
            module.stop();
        }
    }
}
