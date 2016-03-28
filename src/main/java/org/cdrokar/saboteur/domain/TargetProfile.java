package org.cdrokar.saboteur.domain;

import org.cdrokar.saboteur.disruption.provided.Throw;
import org.cdrokar.saboteur.exception.SabotageException;
import org.cdrokar.saboteur.util.ConfigReader;
import org.cdrokar.saboteur.disruption.provided.Throw;
import org.cdrokar.saboteur.exception.InstructionNotFoundException;
import org.cdrokar.saboteur.exception.SabotageException;
import org.cdrokar.saboteur.util.ConfigReader;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by cdrolet on 3/6/2016.
 */

@Data
@Builder
public class TargetProfile {

    public static final TargetProfile DEFAULT = TargetProfile.builder()
            .alias("default")
            .classPath("*")
            .disrupted(false)
            .instructions(ImmutableMap.of(Throw.KEY_BEFORE_EXCEPTION_CLASS, SabotageException.class.getName()))
            .build();

    private final String alias;

    private final String classPath;

    private String method;

    private boolean disrupted;

    private final Map<String, String> instructions;

    public static TargetProfile from(Config config) {

        String classPath = config.getString("classPath");

        return builder()
                .alias(ConfigReader.INSTANCE.read(config, "alias", classPath.substring(classPath.lastIndexOf("."))))
                .classPath(classPath)
                .method(ConfigReader.INSTANCE.read(config, "method", "*"))
                .disrupted(ConfigReader.INSTANCE.read(config, "disrupted", false))
                .instructions(ConfigReader.INSTANCE.read(config, "instructions", Maps.newHashMap()))
                .build();
    }

    @JsonIgnore
    public Predicate<String> getMethodPredicate() {
        if ("*".equals(method)) {
            return (s) -> true;
        }

        return (s) -> s.equalsIgnoreCase(method);
    }

    public String getInstruction(String key) {
        if (!instructions.containsKey(key)) {
            throw new InstructionNotFoundException(alias, key);
        }

        return instructions.get(key);
    }
}
