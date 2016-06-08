package org.cdrokar.saboteur.domain;

import lombok.Data;

import java.util.Collection;
import java.util.stream.Collectors;

import org.cdrokar.saboteur.reader.ConfigReader;

import com.typesafe.config.Config;

@Data
public class Configuration {

    private final boolean displayBanner;

    private final boolean displayConfig;

    private final boolean infiltrateAll;

    private Collection<TargetProfile> targetProfiles;

    public Configuration(Config config) {
        displayBanner = ConfigReader.INSTANCE.read(config, "displayBanner", true);
        displayConfig = ConfigReader.INSTANCE.read(config, "displayConfig", true);
        infiltrateAll = ConfigReader.INSTANCE.read(config, "infiltrateAll", false);

        targetProfiles = config.getConfigList("targets")
                .stream()
                .map(c -> TargetProfile.from(c))
                .collect(Collectors.toList());
    }


}
