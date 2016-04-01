package org.cdrokar.saboteur.util;

import org.cdrokar.saboteur.domain.Configuration;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cdrolet on 3/19/2016.
 */
public enum ConfigReader {

    INSTANCE;

    private static final String DEFAULT_CONFIG_FILE_NAME = "default.conf";
    private static final String CONFIG_FILE_NAME = "saboteur.conf";

    private DefaultResourceLoader defaultLoader = new DefaultResourceLoader();

    public Configuration load() {

        Config defaultConfig = ConfigFactory.load(DEFAULT_CONFIG_FILE_NAME);

        Resource resource = defaultLoader.getResource("classpath:" + CONFIG_FILE_NAME);
        if (!resource.exists()) {
            //TODO need better feedback
            throw new IllegalStateException("RESOURCE NOT FOUND");
        }
        return new Configuration(ConfigFactory.load(CONFIG_FILE_NAME)
                .withFallback(defaultConfig)
                .root()
                .toConfig());
    }

    public Boolean read(Config config, String path, Boolean defaultValue) {
        return config.hasPath(path) ? config.getBoolean(path) : defaultValue;
    }

    public Integer read(Config config, String path, Integer defaultValue) {
        return config.hasPath(path) ? config.getInt(path) : defaultValue;
    }

    public String read(Config config, String path, String defaultValue) {
        return config.hasPath(path) ? config.getString(path) : defaultValue;
    }

    public Map<String, String> read(Config config, String path, Map<String, String> defaultValue) {

        return config.hasPath(path)
                ? config.getConfig(path).entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().unwrapped().toString()))
                : defaultValue;
    }

}
