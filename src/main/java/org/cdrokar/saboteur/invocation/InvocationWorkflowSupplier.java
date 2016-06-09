package org.cdrokar.saboteur.invocation;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.cdrokar.saboteur.Repository;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.domain.Action;
import org.cdrokar.saboteur.domain.Instruction;
import org.cdrokar.saboteur.infiltration.BeanDefinition;

@RequiredArgsConstructor
public class InvocationWorkflowSupplier implements Supplier<InvocationWorkflowSupplier.InvocationWorkflow> {

    private final Repository repository;

    private final BeanDefinition definition;

    private long currentVersion = -1;

    private InvocationWorkflow currentWorkflow;

    public InvocationWorkflow get() {
        if (isVersionChanged()) {
            refresh();
        }

        return currentWorkflow;
    }

    private boolean isVersionChanged() {
        return currentVersion != repository.getVersion();
    }

    private void refresh() {
        currentVersion = repository.getVersion();
        currentWorkflow = transform(definition);
    }

    private InvocationWorkflow transform(BeanDefinition beanDefinition) {

        Action action = repository.getActionFor(beanDefinition);

        return InvocationWorkflow.builder()
                .disrupted(action.isActive())
                .disruptives(repository.getDisruptives(action.getInstructions()))
                .methodFilter(action.getMethodPredicate())
                .instructions(action.getInstructions())
                .build();
    }

    @Builder
    public static class InvocationWorkflow {

        private final boolean disrupted;

        private final Predicate<String> instanceFilter;

        private final Predicate<String> methodFilter;

        private final Collection<Disruptive> disruptives;

        private final List<Instruction> instructions;

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
    }

}
