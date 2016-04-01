package org.cdrokar.saboteur.disruption;


import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.cdrokar.saboteur.SaboteurRepository;
import org.cdrokar.saboteur.SaboteurRepository;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.exception.ValidationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Created by cdrolet on 3/4/2016.
 */


@Component
public class DisruptiveBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    SaboteurRepository repository;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Disruptive) {
            checkInstructionsNotAlreadyExist((Disruptive) bean);
            Disruptive.ALL.add((Disruptive) bean);
        }
        return bean;
    }

    private void checkInstructionsNotAlreadyExist(Disruptive disruptive) {

        Optional<Disruptive> alreadyContain = Disruptive.ALL.stream()
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
