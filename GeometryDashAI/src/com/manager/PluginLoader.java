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

        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            System.out.println("Katalog plugins nie istnieje: " + pluginDir.getAbsolutePath());
            return loadedPlugins;
        }

        File[] files = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            System.out.println("Brak plików JAR w katalogu: " + pluginDir.getAbsolutePath());
            return loadedPlugins;
        }
        System.out.println("Znaleziono " + files.length + " plików JAR w katalogu plugins");

        for (File jarFile : files) {
            try {
                URL jarUrl = jarFile.toURI().toURL();
                ClassLoader parent = PluginLoader.class.getClassLoader();
                URLClassLoader classLoader = new URLClassLoader(
                        new URL[]{jarUrl},
                        parent
                );
                ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(classLoader);

                    ServiceLoader<T> loader = ServiceLoader.load(serviceInterface, classLoader);

                    boolean foundAny = false;
                    for (T plugin : loader) {
                        loadedPlugins.add(plugin);
                        foundAny = true;
                        System.out.println("Załadowano plugin: " +
                                plugin.getClass().getName() + " z " + jarFile.getName());
                    }
                    if (!foundAny) {
                        System.out.println("OSTRZEŻENIE: Nie znaleziono żadnych usług w " + jarFile.getName());
                    }
                } finally {
                    Thread.currentThread().setContextClassLoader(oldContextClassLoader);
                }
            } catch (Exception e) {
                System.err.println("Błąd podczas ładowania pluginu z " + jarFile.getName());
                e.printStackTrace();
            }
        }
        if (loadedPlugins.isEmpty()) {
            System.out.println("UWAGA: Nie załadowano żadnych pluginów. Sprawdź strukturę JAR-ów i pliki META-INF/services");
        }
        return loadedPlugins;
    }
}
