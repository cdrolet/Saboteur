package org.cdrokar.saboteur.validation;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.exception.ValidationException;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by cdrolet on 4/1/2016.
 */
public enum DisruptiveValidator implements Consumer<Disruptive>{

    INSTANCE;

    @Override
    public void accept(Disruptive disruptive) {
        Optional<Disruptive> alreadyContain = Disruptive.REGISTRY.stream()
                .filter(d -> !Collections.disjoint(
                        d.getInstructionKeys(),
                        disruptive.getInstructionKeys()))
                .findFirst();

        if (!alreadyContain.isPresent()) {
            return;
        }

        Collection<String> commonKeys = Lists.newArrayList(alreadyContain.get().getInstructionKeys());

        commonKeys.retainAll(disruptive.getInstructionKeys());

        throw new ValidationException(ValidationException.Type.INSTRUCTION_KEY_ALREADY_DEFINED,
                Joiner.on(",").join(commonKeys),
                disruptive.getClass().getSimpleName(),
                alreadyContain.get().getClass().getSimpleName());

    }
}
