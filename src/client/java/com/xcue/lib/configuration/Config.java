package com.xcue.lib.configuration;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
// TODO: Make this an instanced class which takes in a namespace and feeds to a static config queue

public final class Config {
    private final static Logger logger = Logger.getLogger("aaa");
    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Integer.class, new IntegerSerializer())
            .registerTypeAdapter(Double.class, new DoubleSerializer())
            .registerTypeAdapter(Long.class, new LongSerializer())
            .setPrettyPrinting()
            .create();
    private final static File configFile;
    /**
     * Backup file in case the main file becomes corrupted
     */
    private final static File tmpFile;
    private static Map<String, Object> configMap;

    static {
        Path path = FabricLoader.getInstance().getConfigDir();
        configFile = new File(path.toFile(), "aaa.json");
        configMap = new HashMap<>();
        tmpFile = new File(path.toFile(), "aaa.tmp");
    }

    /**
     * Loads the config from the local json file
     */
    public static void load() {
        load(false);
    }

    /**
     * Loads config settings from the local json file
     *
     * @param fromBackup Whether to load specifically from the backup file
     */
    private static void load(boolean fromBackup) {
        logger.log(Level.INFO, "Attempting to load config" + (fromBackup ? " from backup..." : "..."));
        File file = fromBackup ? tmpFile : configFile;
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                configMap = GSON.fromJson(reader, type);
            } catch (IOException | JsonSyntaxException e) {
                logger.warning(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n")));

                if (e instanceof JsonSyntaxException) {
                    // JsonSyntaxException indicates the file was corrupted, so load from backup
                    load(true);
                    return;
                }
            }
        } else {
            configMap = new HashMap<>();
        }

        // Don't overwrite backup with main json file when pulling from backup
        save(!fromBackup);
    }

    /**
     * Saves config to the local json file and backs up the existing config
     */
    public static void save() {
        save(true);
    }

    /**
     * Saves config to the local json file and the previous version becomes a backup .tmp file
     */
    private static void save(boolean overwriteBackup) {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException ignore) {
                logger.log(Level.WARNING, "Encountered an issue when creating main config file.");
            }
        }

        if (!tmpFile.exists()) {
            try {
                tmpFile.createNewFile();
            } catch (IOException ignore) {
                logger.log(Level.WARNING, "Encountered an issue when creating tmp config file.");
            }
        }

        if (overwriteBackup) {
            boolean deletedOldBackup = tmpFile.delete();
            boolean createdNewBackup = false;

            if (deletedOldBackup) {
                createdNewBackup = configFile.renameTo(tmpFile);
            }

            if (!createdNewBackup) {
                logger.log(Level.WARNING, "Could not backup config. Skipping save.");
                return;
            }
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(configMap, writer);
        } catch (IOException e) {
            logger.warning(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
        }
    }

    public static void set(String key, Object value) {
        configMap.put(key, value);
        save();
    }

    public static void remove(String key, Object value) {
        configMap.remove(key);
        save();
    }

    /**
     * @param key Setting key
     * @param def Default value
     * @param <T> Expected type of the value
     * @return Value or default if not set
     */
    public static <T> T get(String key, T def) {
        Object val = configMap.get(key);

        if (def.getClass().isInstance(val)) {
            return (T) val;
        } else {
            logger.warning("Could not cast type " + def.getClass() + " to type of" + val + " for config " +
                    "setting " + key);
            return def;
        }
    }

    public static void toggle(String key, boolean def) {
        boolean value = get(key, def);
        set(key, !value);
        save();
    }

    @Override
    public String toString() {
        return "Config{" +
                "configMap=" + configMap +
                "}";
    }
}
