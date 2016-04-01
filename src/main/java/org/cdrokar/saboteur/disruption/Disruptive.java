package org.cdrokar.saboteur.disruption;

import com.google.common.collect.Sets;
import org.cdrokar.saboteur.invocation.SaboteurInvocation;
import org.cdrokar.saboteur.invocation.SaboteurInvocation;
import com.google.common.base.Joiner;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by cdrolet on 3/3/2016 .
 */
public interface Disruptive extends Function<SaboteurInvocation, Optional<Object>> {

    Collection<Disruptive> ALL = Sets.newHashSet();

    String DEFAULT_MESSAGE = "Target has been sabotaged from a test or a manual intervention.";

    String getDescription();

    Collection<String> getInstructionKeys();

    default String getDetailFrom(SaboteurInvocation inv) {

        String instructions = Joiner.on(",").join(
                getInstructionKeys().stream()
                        .filter(key -> inv.getInstructions().containsKey(key))
                        .map(key -> key + ": " + inv.getInstructions().get(key))
                        .collect(Collectors.toList()));

        return new StringBuilder(DEFAULT_MESSAGE)
                .append("/n")
                .append(" > Action: ")
                .append(getDescription())
                .append("/n")
                .append(" > Instructions: ")
                .append(instructions)
                .append("/n")
                .append(" > Target: ")
                .append(inv.getBeanDefinition().getClassName())
                .append("/n")
                .append(" > Method: ")
                .append(inv.getMethodInvocation().getMethod().toGenericString())
                .append("/n")
                .toString();
    }

    default Map<String, String> getInstructionFrom(SaboteurInvocation invocation) {

        Map<String, String> instructions = invocation
                .getInstructions().keySet().stream()
                .filter(k -> getInstructionKeys().contains(k))
                .collect(Collectors.toMap(k -> k, k -> invocation.getInstructions().get(k)));

        if (instructions.isEmpty()) {
            throw new IllegalStateException("no instructions found for :" + getClass().getSimpleName());
        }
        return instructions;
    }
}
