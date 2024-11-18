package net.vitacraft.api.classloader;

import net.vitacraft.api.MBModule;
import net.vitacraft.exceptions.CircularDependencyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Utility class for loading modules from JAR files.
 * <p>
 * This class provides a utility method for loading modules from JAR files
 * in a specified directory. The modules are loaded using a URLClassLoader
 * and are sorted based on their dependencies.
 * </p>
 */
public class ModuleLoader {
    private static final Logger logger = LoggerFactory.getLogger("MoBot");

    /**
     * Loads modules from JAR files in the specified directory.
     *
     * @param modulesPath the path to the directory containing the module JAR files
     * @return a list of loaded modules sorted based on their dependencies
     */
    public static List<MBModule> loadModules(String modulesPath) {
        List<MBModule> modules = new ArrayList<>();
        File modulesDir = new File(modulesPath);
        if (modulesDir.isDirectory()) {
            File[] jarFiles = modulesDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (jarFiles != null) {
                Map<String, MBModule> moduleMap = new HashMap<>();
                Map<String, Set<String>> dependencyGraph = new HashMap<>();
                for (File jarFile : jarFiles) {
                    try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, ModuleLoader.class.getClassLoader())) {
                        List<MBModule> loadedModules = loadModulesFromClassLoader(classLoader);
                        for (MBModule module : loadedModules) {
                            String moduleName = module.getModuleInfo().name();
                            moduleMap.put(moduleName, module);
                            dependencyGraph.putIfAbsent(moduleName, new HashSet<>());
                            net.vitacraft.api.classloader.ModuleConfigReader.readConfig(jarFile, moduleName, dependencyGraph);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to load JAR file: {}", jarFile.getName(), e);
                    }
                }
                try {
                    List<String> sortedModuleNames = net.vitacraft.api.classloader.ModuleSorter.topologicalSort(dependencyGraph);
                    for (String moduleName : sortedModuleNames) {
                        modules.add(moduleMap.get(moduleName));
                    }
                } catch (CircularDependencyException e) {
                    logger.error("Failed to sort modules: {}", e.getMessage());
                }
            } else {
                logger.warn("No JAR files found in the modules directory.");
            }
        } else {
            logger.error("Modules directory is not a directory.");
        }
        return modules;
    }

    /**
     * Loads modules from a URLClassLoader.
     *
     * @param classLoader the URLClassLoader to load classes from
     * @return a list of loaded modules
     */
    private static List<MBModule> loadModulesFromClassLoader(URLClassLoader classLoader) {
        List<MBModule> modules = new ArrayList<>();
        try {
            for (Class<?> cls : getClassesFromClassLoader(classLoader)) {
                if (MBModule.class.isAssignableFrom(cls) && !cls.isInterface()) {
                    MBModule module = (MBModule) cls.getDeclaredConstructor().newInstance();
                    modules.add(module);
                    logger.info("Loaded module: {}", module.getModuleInfo().name());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load classes from class loader", e);
        }
        return modules;
    }

    /**
     * Gets a list of classes from a URLClassLoader.
     *
     * @param classLoader the URLClassLoader to get classes from
     * @return a list of loaded classes
     * @throws Exception if an error occurs while loading classes
     */
    private static List<Class<?>> getClassesFromClassLoader(URLClassLoader classLoader) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        for (URL url : classLoader.getURLs()) {
            File jarFile = new File(url.toURI());
            try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile)) {
                java.util.Enumeration<java.util.jar.JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    java.util.jar.JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName().replace("/", ".").replace(".class", "");
                        try {
                            Class<?> cls = classLoader.loadClass(className);
                            classes.add(cls);
                        } catch (ClassNotFoundException e) {
                            logger.error("Class not found: {}", className, e);
                        }
                    }
                }
            }
        }
        return classes;
    }
}