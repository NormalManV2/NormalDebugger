package org.normal.normalDebugger.event.scanner;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class BukkitEventScanner {

    public static Set<Class<? extends Event>> getAllEvents(JavaPlugin plugin) {
        Set<Class<? extends Event>> result = new HashSet<>();
        String path = "org/bukkit/event";

        try {
            CodeSource codeSource = Event.class.getProtectionDomain().getCodeSource();
            if (codeSource == null) return result;

            URL jarUrl = codeSource.getLocation();
            try (JarInputStream jarStream = new JarInputStream(jarUrl.openStream())) {
                JarEntry entry;
                while ((entry = jarStream.getNextJarEntry()) != null) {
                    String name = entry.getName();

                    if (!name.startsWith(path) || !name.endsWith(".class")) continue;

                    String className = name.replace("/", ".").replace(".class", "");
                    try {
                        Class<?> clazz = Class.forName(className, false, plugin.getClass().getClassLoader());

                        if (Event.class.isAssignableFrom(clazz)
                                && hasHandlerList(clazz)
                                && !Modifier.isAbstract(clazz.getModifiers())
                                && !clazz.isInterface()) {
                            result.add(clazz.asSubclass(Event.class));
                        }
                    } catch (Throwable ignored) {

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static boolean hasHandlerList(Class<?> clazz) {
        try {
            Method method = clazz.getMethod("getHandlerList");
            return Modifier.isStatic(method.getModifiers()) &&
                    HandlerList.class.isAssignableFrom(method.getReturnType());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
