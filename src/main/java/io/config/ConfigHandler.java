package io.config;

import com.google.common.base.MoreObjects;
import com.google.gson.*;
import io.IDBCredentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * A handler for json configurations
 *
 * @author Cedric Richter
 */
public class ConfigHandler {

    public static final String DEFAULT_CONFIG = "system_config";
    private static final String LOCALIZED_PATH_PREFIX = "./config/";
    private static final String PATH_PREFIX = "./config/";
    private static final String CONFIG_POSTFIX = ".json";
    private static Map<String, ConfigHandler> handlerMap = new HashMap<>();


    private Gson parser;


    private Map<String, JsonElement> preloadMap = new HashMap<>();
    private Map<String, Object> loadedMap = new HashMap<>();


    private Gson initParser(){
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeHierarchyAdapter(IDBCredentials.class, new CredentialDeserializer());

        return builder.create();
    }


    ConfigHandler() {
        this.parser = initParser();
    }


    /**
     * Loads a default configuration.
     * The default configuration should be placed relative to the program in
     * /config/system_config.json
     * @return a handler for the default configuration
     */
    public static ConfigHandler getDefault() {
        if (handlerMap.containsKey(DEFAULT_CONFIG)) {
            return handlerMap.get(DEFAULT_CONFIG);
        }
        return createCustom(DEFAULT_CONFIG);
    }

    /**
     * Loads a custom config.
     *
     * It will look for a file: ./config/%name%.json
     *
     * You can preload configs from custom locations with {@see injectCustomConfig}.
     *
     * @param name name of the configuration
     * @return a handler for the custom configuration
     */
    public static ConfigHandler createCustom(String name) {
        if (handlerMap.containsKey(name)) {
            return handlerMap.get(name);
        }
        ConfigHandler handler = new ConfigHandler();
        handler.loadFile(name);
        return handler;
    }

    /**
     * Injects a custom config from a custom location.
     * @param name name of the configuration
     * @param p location of the configuration
     */
    public static void injectCustomConfig(String name, Path p) {
        if (!handlerMap.containsKey(name)) {
            ConfigHandler handler = new ConfigHandler();
            handler.loadCustomPath(name, p);
            handlerMap.put(name, handler);
        }
    }

    /**
     * Loads an entry of the configuration
     * @param name name of the object
     * @param clazz class of the object
     * @param <T> return type
     * @return a object which is deserialized to the given type
     */
    public <T> T getConfig(String name, Class<T> clazz) {
        if (loadedMap.containsKey(name)) {
            return (T) loadedMap.get(name);
        }
        if (preloadMap.containsKey(name)) {
            try {
                T out = parser.fromJson(preloadMap.remove(name), clazz);
                loadedMap.put(name, out);
                return out;
            } catch (Exception e) {
                System.out.println("Cannot parse json element as: " + e.getLocalizedMessage());
            }
        }
        return null;
    }

    /**
     * Same as getConfig for a class type but will return a default value if the config
     * cannot be handled.
     * @param name name of the object
     * @param defaultObj default return value
     * @param <T> return type
     * @return a object which is deserialized to the given type or default value
     */
    public <T> T getConfig(String name, T defaultObj) {
        return MoreObjects.firstNonNull(getConfig(name, (Class<T>) defaultObj.getClass()), defaultObj);
    }


    private void loadFile(String name) {
        Path p = Paths.get(PATH_PREFIX + name + CONFIG_POSTFIX);
        loadCustomPath(name, p);
    }


    private void loadCustomPath(String name, Path p) {
        if (Files.notExists(p)) {
            fallback(name, p);
        }
        String in = "";
        try {
            BufferedReader reader = Files.newBufferedReader(p);
            String act;
            while ((act = reader.readLine()) != null) in += act;
            JsonElement element = new JsonParser().parse(in);
            if (element instanceof JsonObject) {
                JsonObject obj = (JsonObject) element;
                for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                    preloadMap.put(e.getKey(), e.getValue());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load: "+p);
        }
    }

    private void fallback(String name, Path p) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                LOCALIZED_PATH_PREFIX + name + CONFIG_POSTFIX);
        if (stream != null) {
            try {
                Path parent = p.getParent();
                if (parent != null)
                    Files.createDirectory(parent);

                OutputStream outputStream = Files.newOutputStream(p);
                int i = -1;
                while ((i = stream.read()) != -1) outputStream.write(i);
                outputStream.flush();
                outputStream.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
