package org.cdrokar.saboteur.domain;

import lombok.Data;

import java.util.Collection;
import java.util.stream.Collectors;

import com.typesafe.config.Config;

@Data
public class Configuration {

    private final boolean displayBanner;

    private final boolean displayConfig;

    private final boolean infiltrateAll;

    private Collection<TargetProfile> targetProfiles;

    public Configuration(Config config) {
        displayBanner = config.getBoolean("displayBanner");
        displayConfig = config.getBoolean("displayConfig");
        infiltrateAll = config.getBoolean("infiltrateAll");

        targetProfiles = config.getConfigList("targets")
                .stream()
                .map(c -> TargetProfile.from(c))
                .collect(Collectors.toList());
    }


}
