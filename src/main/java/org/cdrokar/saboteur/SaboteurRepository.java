package org.cdrokar.saboteur;

import lombok.Getter;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.domain.BeanDefinition;
import org.cdrokar.saboteur.domain.Configuration;
import org.cdrokar.saboteur.domain.TargetProfile;
import org.cdrokar.saboteur.exception.ValidationException;
import org.cdrokar.saboteur.infiltration.TargetMatchPredicate;
import org.cdrokar.saboteur.reader.ConfigReader;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by cdrolet on 3/3/2016.
 */
@Component
public class SaboteurRepository {

    private final AtomicLong version = new AtomicLong(0);

    @Getter
    private final Configuration configuration;

    private final Map<String, TargetProfile> targetProfileByPath;

    private final Map<String, TargetProfile> targetProfilesByAlias;

    public SaboteurRepository() {

        configuration = ConfigReader.INSTANCE.load();

        targetProfilesByAlias = configuration
                .getTargetProfiles()
                .stream()
                .collect(Collectors.toMap(t -> t.getAlias(), t -> t));

        targetProfileByPath = configuration
                .getTargetProfiles()
                .stream()
                .collect(Collectors.toMap(t -> t.getClassPath(), t -> t));

    }

    public Collection<TargetProfile> getTargets() {
        return targetProfileByPath.values();
    }

    public TargetProfile getTarget(String target) {
        TargetProfile profile = targetProfilesByAlias.get(target);
        if (profile == null) {
            profile = targetProfileByPath.get(target);
        }
        if (profile == null) {
            throw new ValidationException(ValidationException.Type.TARGET_NOT_FOUND, target);
        }
        return profile;
    }

    public Long getVersion() {
        return version.get();
    }

    public TargetProfile getTargetProfileFor(BeanDefinition beanDefinition) {
        return targetProfileByPath.keySet()
                .stream()
                .filter(path -> TargetMatchPredicate.INSTANCE.test(beanDefinition, path))
                // TODO can't work for match parent type
                .sorted(Comparator.reverseOrder())
                // ---
                .map(path -> targetProfileByPath.get(path))
                .findFirst()
                .orElse(TargetProfile.DEFAULT);
    }

    public Collection<Disruptive> getDisruptives(Collection<String> instructions) {
        return Disruptive.REGISTRY.stream()
                .filter(d -> !Collections.disjoint(instructions, d.getInstructionKeys()))
                .collect(Collectors.toList());
    }

    public boolean isBeanInfiltrated(BeanDefinition beanDefinition) {
        if (configuration.isInfiltrateAll()) {
            return true;
        }

        return targetProfileByPath.keySet()
                .stream()
                .anyMatch(profile -> TargetMatchPredicate.INSTANCE.test(beanDefinition, profile));
    }


}
