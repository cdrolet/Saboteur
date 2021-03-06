package org.cdrokar.saboteur.disruption;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.cdrokar.saboteur.invocation.SaboteurInvocation;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public interface Disruptive extends Function<SaboteurInvocation, Optional<Object>> {

    Collection<Disruptive> REGISTRY = Sets.newHashSet();

    String DEFAULT_MESSAGE = "Target has been sabotaged from a test or a manual intervention.";

    String getDescription();

    Collection<String> getInstructionKeys();

    default String getDetailFrom(SaboteurInvocation inv) {

        String instructions = Joiner.on(",").join(
                inv.getInstructions()
                        .stream()
                        .filter(i -> getInstructionKeys().contains(i.getKey()))
                        .map(i -> i.getKey() + ": " + i.getValue())
                        .collect(Collectors.toList()));

        return new StringBuilder(DEFAULT_MESSAGE)
                .append("\n")
                .append(" > Action: ")
                .append(getDescription())
                .append("\n")
                .append(" > Instructions: ")
                .append(instructions)
                .append("\n")
                .append(" > Target: ")
                .append(inv.getBeanDefinition().getClassName())
                .append("\n")
                .append(" > Method: ")
                .append(inv.getMethodInvocation().getMethod().toGenericString())
                .append("\n")
                .toString();
    }

    default Map<String, String> getInstructionFrom(SaboteurInvocation invocation) {

        Map<String, String> instructions = invocation
                .getInstructions()
                .stream()
                .filter(i -> getInstructionKeys().contains(i.getKey()))
                .collect(Collectors.toMap(i -> i.getKey(), i -> i.getValue()));

        if (instructions.isEmpty()) {
            throw new IllegalStateException("no instructions found for :" + getClass().getSimpleName());
        }
        return instructions;
    }
}
