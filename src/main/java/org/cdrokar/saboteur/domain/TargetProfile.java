package org.cdrokar.saboteur.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.cdrokar.saboteur.reader.ConfigReader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetProfile {

    public static final TargetProfile DEFAULT = TargetProfile.builder()
            .alias("default")
            .classPath("*")
            .disrupted(false)
            .instructions(ImmutableList.of(Instruction.DEFAULT))
            .build();

    private static final Set<String> aliases = Sets.newHashSet();

    private String alias;

    private String classPath;

    private boolean targetSubclass;

    private String method;

    private boolean disrupted;

    private List<Instruction> instructions;

    public static TargetProfile from(Config config) {

        String classPath = config.getString("classPath");

        String alias = getUniqueAlias(ConfigReader.INSTANCE.read(config, "alias", classPath.substring(classPath.lastIndexOf("."))));

        TargetProfile profile = builder()
                .alias(alias)
                .classPath(classPath)
                .method(ConfigReader.INSTANCE.read(config, "method", DEFAULT.method))
                .disrupted(ConfigReader.INSTANCE.read(config, "disrupted", DEFAULT.disrupted))
                .instructions(Lists.reverse(config.getConfigList("instructions")
                        .stream()
                        .map(c -> Instruction.from(c))
                        .collect(Collectors.toList())))
                .targetSubclass(ConfigReader.INSTANCE.read(config, "targetSubclass", DEFAULT.targetSubclass))
                .build();

        return profile;
    }

    private static String getUniqueAlias(String source) {
        String uniqueAlias = source;
        int count = 0;
        while (aliases.contains(uniqueAlias)) {
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

/*
    public String getInstruction(String key) {
        return instructions.get(key);
    }

    public void setInstructions(Map<String, String> instructions) {
        instructions.forEach((k, v) -> setInstruction(k, v));
    }

    public void setInstruction(String key, String value) {
        instructions.put(key, value);
    }
*/

}
