package org.cdrokar.saboteur.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.cdrokar.saboteur.reader.ConfigReader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Action implements Comparator<Action> {

    public static final Action DEFAULT = Action.builder()
            .name("default")
            .targetClassPath("*")
            .withSubclass(false)
            .active(false)
            .instructions(ImmutableList.of(Instruction.DEFAULT))
            .build();

    private static final Set<String> names = Sets.newHashSet();

    private String name;

    private String targetClassPath;

    private boolean withSubclass;

    private String method;

    private boolean active;

    private List<Instruction> instructions;

    public static Action from(Config config) {

        String classPath = config.getString("targetClassPath");

        String name = getUniqueName(ConfigReader.INSTANCE.read(
                config,
                "name",
                classPath.substring(classPath.lastIndexOf("."))));

        Action action = builder()
                .name(name)
                .targetClassPath(classPath)
                .method(ConfigReader.INSTANCE.read(config, "method", DEFAULT.method))
                .active(ConfigReader.INSTANCE.read(config, "active", DEFAULT.active))
                .instructions(config.getConfigList("instructions")
                        .stream()
                        .map(c -> Instruction.from(c))
                        .collect(Collectors.toList()))
                .withSubclass(ConfigReader.INSTANCE.read(config, "withSubclass", DEFAULT.withSubclass))
                .build();

        return action;
    }

    private static String getUniqueName(String source) {
        String uniqueName = source;
        int count = 0;
        while (names.contains(uniqueName)) {
            uniqueName = source + "(" + ++count + ")";
        }
        names.add(uniqueName);
        return uniqueName;
    }

    @JsonIgnore
    public Predicate<String> getMethodPredicate() {
        if ("*".equals(method)) {
            return (s) -> true;
        }

        return (s) -> s.equalsIgnoreCase(method);
    }

    public int compare(Action o1, Action o2) {

        if (o1.equals(o2)) {
            return 0;
        }

        // Default profile is the last one
        if (o1.equals(Action.DEFAULT)) {
            return 1;
        }

        if (o2.equals(Action.DEFAULT)) {
            return -1;
        }

        // Subclasses profiles are taken last
        if (o1.isWithSubclass() && !o2.isWithSubclass()) {
            return 1;
        }

        if (o2.isWithSubclass() && !o1.isWithSubclass()) {
            return -1;
        }

        // Wildcards profiles are taken last
        if (o1.getTargetClassPath().contains("*") && !o2.getTargetClassPath().contains("*")) {
            return 1;
        }

        if (o2.getTargetClassPath().contains("*") && !o1.getTargetClassPath().contains("*")) {
            return -1;
        }

        // Classpath length (longer path taken precedence)
        return Integer.compare(o2.getTargetClassPath().length(), o1.getTargetClassPath().length());
    }

}
