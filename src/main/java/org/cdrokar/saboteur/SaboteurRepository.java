package org.cdrokar.saboteur;

import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.domain.BeanDefinition;
import org.cdrokar.saboteur.domain.Configuration;
import org.cdrokar.saboteur.domain.Instruction;
import org.cdrokar.saboteur.domain.TargetProfile;
import org.cdrokar.saboteur.exception.ValidationException;
import org.cdrokar.saboteur.infiltration.TargetProfileComparator;
import org.cdrokar.saboteur.infiltration.TargetProfileSelector;
import org.cdrokar.saboteur.reader.ConfigReader;
import org.cdrokar.saboteur.validation.TargetProfileValidator;
import org.springframework.stereotype.Component;

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

    public void saveProfile(TargetProfile profile) {

        TargetProfileValidator.INSTANCE.accept(profile);

        targetProfileByPath.put(profile.getClassPath(), profile);

        targetProfilesByAlias.put(profile.getAlias(), profile);

        version.incrementAndGet();
    }

    public TargetProfile getTargetProfile(String target) {
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
        return targetProfileByPath.values()
                .stream()
                .sorted(TargetProfileComparator.INSTANCE)
                .filter(path -> TargetProfileSelector.INSTANCE.test(beanDefinition, path))
                .findFirst()
                .orElse(TargetProfile.DEFAULT);
    }

    public Collection<Disruptive> getDisruptives(Collection<Instruction> instructions) {

        Collection<String> instructionKeys = instructions.stream().map((i) -> i.getKey()).collect(Collectors.toList());
        return Disruptive.REGISTRY.stream()
                .filter(d -> !Collections.disjoint(instructionKeys, d.getInstructionKeys()))
                .collect(Collectors.toList());
    }

    public boolean isBeanInfiltrated(BeanDefinition beanDefinition) {

        if (configuration.isInfiltrateAll()) {
            return true;
        }

        return getTargetProfileFor(beanDefinition) != TargetProfile.DEFAULT;
    }
}
