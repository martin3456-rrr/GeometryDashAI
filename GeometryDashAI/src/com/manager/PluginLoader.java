package com.manager;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class PluginLoader {

    public static <T> List<T> loadPlugins(String directoryPath, Class<T> serviceInterface) {
        List<T> loadedPlugins = new ArrayList<>();
        File pluginDir = new File(directoryPath);
        if (!pluginDir.exists() || !pluginDir.isDirectory()) return loadedPlugins;

        File[] files = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) return loadedPlugins;

        for (File jarFile : files) {
            try {
                URL jarUrl = jarFile.toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl});

                ServiceLoader<T> loader = ServiceLoader.load(serviceInterface, classLoader);

                for (T plugin : loader) {
                    loadedPlugins.add(plugin);
                    System.out.println("Załadowano plugin: " + plugin.getClass().getName() + " z " + jarFile.getName());
                }
            } catch (Exception e) {
                System.err.println("Błąd podczas ładowania pluginu z " + jarFile.getName());
                e.printStackTrace();
            }
        }
        return loadedPlugins;
    }
}