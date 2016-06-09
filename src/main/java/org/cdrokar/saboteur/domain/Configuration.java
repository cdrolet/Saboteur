package org.cdrokar.saboteur.domain;

import lombok.Data;

import java.util.Collection;
import java.util.stream.Collectors;

import org.cdrokar.saboteur.reader.ConfigReader;

import com.typesafe.config.Config;

@Data
public class Configuration {

    private final boolean displayBanner;

    private Collection<Action> actions;

    public Configuration(Config config) {
        displayBanner = ConfigReader.INSTANCE.read(config, "displayBanner", true);

        actions = config.getConfigList("actions")
                .stream()
                .map(c -> Action.from(c))
                .collect(Collectors.toList());
    }


}
