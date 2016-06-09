package org.cdrokar.saboteur;

import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.domain.Action;
import org.cdrokar.saboteur.domain.Configuration;
import org.cdrokar.saboteur.domain.Instruction;
import org.cdrokar.saboteur.exception.ValidationException;
import org.cdrokar.saboteur.infiltration.BeanActionMatcher;
import org.cdrokar.saboteur.infiltration.BeanDefinition;
import org.cdrokar.saboteur.reader.ConfigReader;
import org.cdrokar.saboteur.validation.ActionValidator;
import org.springframework.stereotype.Component;

@Component
public class Repository {

    private final AtomicLong version = new AtomicLong(0);

    @Getter
    private final Configuration configuration;

    private final Map<String, Action> actionByName;

    public Repository() {

        configuration = ConfigReader.INSTANCE.load();

        actionByName = configuration
                .getActions()
                .stream()
                .collect(Collectors.toMap(a -> a.getName(), a -> a));
    }

    public Collection<Action> getActions() {
        return actionByName.values();
    }

    public long getVersion() {
        return version.get();
    }

    public Action getAction(String action) {
        Action profile = actionByName.get(action);
        if (profile == null) {
            throw new ValidationException(ValidationException.Type.ACTION_NOT_FOUND, action);
        }
        return profile;
    }

    public boolean isAnyActionMatchBean(BeanDefinition beanDefinition) {
        return getActionFor(beanDefinition) != Action.DEFAULT;
    }

    public Action getActionFor(BeanDefinition beanDefinition) {
        return getActions()
                .stream()
                .sorted()
                .filter(action -> BeanActionMatcher.INSTANCE.test(beanDefinition, action))
                .findFirst()
                .orElse(Action.DEFAULT);
    }

    public Collection<Disruptive> getDisruptives(Collection<Instruction> instructions) {

        Collection<String> instructionKeys = instructions.stream().map((i) -> i.getKey()).collect(Collectors.toList());
        return Disruptive.REGISTRY.stream()
                .filter(d -> !Collections.disjoint(instructionKeys, d.getInstructionKeys()))
                .collect(Collectors.toList());
    }

    public void saveActions(Collection<Action> actions) {

        actions.stream().forEach(p -> ActionValidator.INSTANCE.accept(p));

        actions.stream().forEach(p -> saveToMap(p));

        updateVersion();
    }

    public void saveAction(Action target) {

        ActionValidator.INSTANCE.accept(target);

        saveToMap(target);

        updateVersion();
    }

    private void saveToMap(Action action) {
        actionByName.put(action.getName(), action);
    }

    public void setActive(String name, boolean value) {
        getAction(name).setActive(Boolean.valueOf(value));
        updateVersion();
    }

    public void setInstructions(String name, List<Instruction> instructions) {
        getAction(name).setInstructions(instructions);
        updateVersion();
    }

    private void updateVersion() {
        version.incrementAndGet();
    }


}
