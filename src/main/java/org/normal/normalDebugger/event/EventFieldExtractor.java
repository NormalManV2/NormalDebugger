package org.normal.normalDebugger.event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class EventFieldExtractor {

    public static Map<String, Object> extractFields(Event event) {
        Map<String, Object> fields = new LinkedHashMap<>();
        Class<?> clazz = event.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (!field.canAccess(event)) field.setAccessible(true);

            try {
                Object value = field.get(event);
                if (isBasicType(value)) {
                    fields.put(field.getName(), value);
                } else if (value instanceof Entity entity) {
                    fields.put(field.getName(), entity.getType().name() + "[" + entity.getName() + "]");
                } else if (value instanceof Location location) {
                    fields.put(field.getName(), formatLocation(location));
                } else if (value instanceof Enum<?>) {
                    fields.put(field.getName(), value.toString());
                }
            } catch (IllegalAccessException e) {
                Bukkit.getLogger().warning("Failed to access field " + field.getName() + " in " + clazz.getName());
            }
        }

        return fields;
    }

    private static boolean isBasicType(Object o) {
        return o instanceof String || o instanceof Number || o instanceof Boolean;
    }

    private static String formatLocation(Location loc) {
        return "Loc[" + Objects.requireNonNull(loc.getWorld()).getName() + " " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "]";
    }
}
