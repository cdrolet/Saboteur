package org.cdrokar.saboteur.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import lombok.Builder;
import lombok.Data;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.disruption.provided.Throw;
import org.cdrokar.saboteur.exception.SabotageException;
import org.cdrokar.saboteur.exception.ValidationException;
import org.cdrokar.saboteur.util.ConfigReader;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private String alias;

    private String classPath;

    private String method;

    private boolean disrupted;

    private final Map<String, String> instructions;

    public static TargetProfile from(Config config) {

        String classPath = config.getString("classPath");

        return builder()
                //TODO may not work when multiple profile contain *
                .alias(ConfigReader.INSTANCE.read(config, "alias", classPath.substring(classPath.lastIndexOf("."))))
                .classPath(classPath)
                .method(ConfigReader.INSTANCE.read(config, "method", "*"))
                .disrupted(ConfigReader.INSTANCE.read(config, "disrupted", false))
                .instructions(ConfigReader.INSTANCE.read(config, "instructions", Maps.newHashMap()))
                .build();
    }

    @PostConstruct
    private void validateContent() {

        //TODO check alias

        checkClassPath();

        checkMethod();

        instructions.keySet().stream().forEach((key) -> checkInstructionKeyExist(key));

        instructions.values().stream().forEach((value) -> checkInstructionIsNotEmpty(value));

    }

    @JsonIgnore
    public Predicate<String> getMethodPredicate() {
        if ("*".equals(method)) {
            return (s) -> true;
        }

        return (s) -> s.equalsIgnoreCase(method);
    }

    public String getInstruction(String key) {
        checkInstructionKeyExist(key);
        checkInstructionKeyIsUsed(key);
        return instructions.get(key);
    }

    public void setInstructions(Map<String, String> instructions) {
        instructions.forEach((k, v) -> setInstruction(k, v));
    }

    public void setInstruction(String key, String value) {
        checkInstructionKeyExist(key);
        checkInstructionIsNotEmpty(value);
        instructions.put(key, value);
    }

    private void checkInstructionKeyExist(String key) {
        Disruptive.ALL.stream()
                .filter(disruptive -> disruptive.getInstructionKeys().contains(key))
                .findAny()
                .orElseThrow(() -> new ValidationException(
                        ValidationException.Type.UNKNOWN_INSTRUCTION_KEY,
                        key,
                        Joiner.on(",").join(
                                Disruptive.ALL
                                        .stream()
                                        .map(d -> d.getInstructionKeys())
                                        .collect(Collectors.toList()))));
    }

    private void checkInstructionIsNotEmpty(String value) {
        if (Strings.isNullOrEmpty(value)) {
            throw new ValidationException(ValidationException.Type.INSTRUCTION_IS_EMPTY, value);
        }
    }

    private void checkInstructionKeyIsUsed(String key) {
        if (!instructions.containsKey(key)) {
            throw new ValidationException(ValidationException.Type.INSTRUCTION_NOT_FOUND, alias, key);
        }
    }

    private void checkClassPath() {

        if (classPath.contains("*")) {
            return;
        }

        if (!getClassFromPath(classPath).isPresent()) {
            throw new ValidationException(ValidationException.Type.INVALID_CLASS_PATH, classPath);
        }
    }

    private void checkMethod() {
        if (method.equals("*") || classPath.contains("*")) {
            return;
        }

        Optional<Class<?>> clazz = getClassFromPath(classPath);
        if (!clazz.isPresent()) {
            return;
        }

        Arrays.stream(clazz.get().getMethods())
                .filter((classMethod) -> classMethod.getName().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ValidationException.Type.INVALID_METHOD, method, classPath));
    }

    private Optional<Class<?>> getClassFromPath(String classPath) {
        try {
            // Spring factory bean path started with a "&"
            return Optional.of(Class.forName(classPath.replace("&", "")));
        } catch (ClassNotFoundException ex) {
            return Optional.empty();
        }

    }
}
