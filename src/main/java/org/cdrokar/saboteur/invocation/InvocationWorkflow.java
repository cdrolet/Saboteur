package org.cdrokar.saboteur.invocation;

import lombok.Builder;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.cdrokar.saboteur.disruption.Disruptive;

@Builder
public class InvocationWorkflow {

    private final boolean disrupted;

    private final Predicate<String> instanceFilter;

    private final Predicate<String> methodFilter;

    private final Collection<Disruptive> disruptives;

    private final Map<String, String> instructions;

    public Object invoke(SourceInvocation sourceInvocation) throws Throwable {

        Optional<Object> result = apply(sourceInvocation);

        if (result.filter(o -> o.equals(Void.class)).isPresent()) {
            return null;
        }

        if (result.isPresent()) {
            return result.get();
        }

        return sourceInvocation.proceed();
    }

    public Optional<Object> apply(SourceInvocation sourceInvocation) {

        if (!isDisrupted(sourceInvocation.getMethodInvocation().getMethod().getName())) {
            return Optional.empty();
        }

        SaboteurInvocation saboteurInvocation = new SaboteurInvocation(sourceInvocation, instructions);

        return disruptives.stream()
                .map(d -> d.apply(saboteurInvocation))
                .filter(o -> o.isPresent())
                .findFirst()
                .get();

    }

    private boolean isDisrupted(String methodName) {

        if (!methodFilter.test(methodName)) {
            return false;
        }

        return disrupted;
    }

/*
    public String getDescription() {

        StringBuilder sb = new StringBuilder(
                Strings.padEnd(name, 15, ' '))
                .append(" | ")
                .append(Strings.padEnd(NO_FILTER.equalsIgnoreCase(targetFilter) ? "{all}" : targetFilter, 15, ' '))
                .append(" | ")
                .append(getState());

        if (Disruptable.State.DISRUPTED.equals(getState())) {
            sb.append(" > ").append(Strings.padEnd(getDisruptiveAction().toString(), 30, ' '));
        }

        return sb.toString();
    }
*/
}
