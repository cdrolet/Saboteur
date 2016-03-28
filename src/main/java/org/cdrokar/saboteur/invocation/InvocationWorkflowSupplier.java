package org.cdrokar.saboteur.invocation;

import org.cdrokar.saboteur.domain.BeanDefinition;
import org.cdrokar.saboteur.SaboteurRepository;
import org.cdrokar.saboteur.domain.BeanDefinition;
import org.cdrokar.saboteur.domain.TargetProfile;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

/**
 * Created by cdrolet on 3/3/2016.
 */

@RequiredArgsConstructor
public class InvocationWorkflowSupplier implements Supplier<InvocationWorkflow> {

    private final SaboteurRepository repository;

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

        TargetProfile targetProfile = repository.getTargetProfileFor(beanDefinition);

        return InvocationWorkflow.builder()
                .disrupted(targetProfile.isDisrupted())
                .disruptives(repository.getDisruptives(targetProfile.getInstructions().keySet()))
                .methodFilter(targetProfile.getMethodPredicate())
                .instructions(targetProfile.getInstructions())
                .build();
    }

}
