package org.cdrokar.saboteur.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.cdrokar.saboteur.disruption.Throw;
import org.cdrokar.saboteur.exception.SabotageException;
import org.cdrokar.saboteur.reader.ConfigReader;
import org.cdrokar.saboteur.validation.TargetProfileValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;

@Data
@Builder
public class TargetProfile {

    public static final TargetProfile DEFAULT = TargetProfile.builder()
            .alias("default")
            .classPath("*")
            .disrupted(false)
            .instructions(ImmutableMap.of(Throw.KEY_BEFORE_EXCEPTION_CLASS, SabotageException.class.getName()))
            .build();

    private static final Set<String> aliases = Sets.newHashSet();

    private String alias;

    private String classPath;

    private boolean targetSubclass;

    private String method;

    private boolean disrupted;

    private final Map<String, String> instructions;

    public static TargetProfile from(Config config) {

        String classPath = config.getString("classPath");

        String alias = getUniqueAlias(ConfigReader.INSTANCE.read(config, "alias", classPath.substring(classPath.lastIndexOf("."))));

        TargetProfile profile = builder()
                .alias(alias)
                .classPath(classPath)
                .method(ConfigReader.INSTANCE.read(config, "method", "*"))
                .disrupted(ConfigReader.INSTANCE.read(config, "disrupted", false))
                .instructions(ConfigReader.INSTANCE.read(config, "instructions", Maps.newHashMap()))
                .targetSubclass(ConfigReader.INSTANCE.read(config, "targetSubclass", false))
                .build();

        TargetProfileValidator.INSTANCE.accept(profile);

        return profile;
    }

    private static String getUniqueAlias(String source) {
        String uniqueAlias = source;
        int count = 0;
        while(aliases.contains(uniqueAlias)) {
            uniqueAlias = source + "(" + ++count + ")";
        }
        aliases.add(uniqueAlias);
        return uniqueAlias;
    }

    @JsonIgnore
    public Predicate<String> getMethodPredicate() {
        if ("*".equals(method)) {
            return (s) -> true;
        }

        return (s) -> s.equalsIgnoreCase(method);
    }

    public String getInstruction(String key) {
        return instructions.get(key);
    }

    public void setInstructions(Map<String, String> instructions) {
        instructions.forEach((k, v) -> setInstruction(k, v));
    }

    public void setInstruction(String key, String value) {
        instructions.put(key, value);
    }

}
