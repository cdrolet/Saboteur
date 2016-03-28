package org.cdrokar.saboteur.disruption.provided;

import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.invocation.SaboteurInvocation;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.invocation.SaboteurInvocation;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by cdrolet on 3/4/2016.
 */
@Component
@Slf4j
public class Delay implements Disruptive {

    public static final String DESCRIPTION = "Delay the execution";

    public static final String KEY_DELAY_MILLI = "delayInMilliseconds";

    private static final Collection<String> INSTRUCTIONS = ImmutableList.of(KEY_DELAY_MILLI);

    @Override
    public Collection<String> getInstructionKeys() {
        return INSTRUCTIONS;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Optional<Object> apply(SaboteurInvocation invocation) {

        Long delayInMilli;
        try {
            delayInMilli = Long.parseLong(getInstructionFrom(invocation).get(KEY_DELAY_MILLI));
        } catch (Exception ex) {
            throw new RuntimeException("Error occur when reading instruction for " + getClass().getSimpleName(), ex);
        }

        log.info(getDetailFrom(invocation));

        try {
            Thread.sleep(delayInMilli);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while proceeding with "
                    + this.getClass().getSimpleName().toUpperCase(), e);
        }

        return Optional.empty();
    }
}
