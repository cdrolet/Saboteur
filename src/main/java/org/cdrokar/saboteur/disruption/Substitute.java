package org.cdrokar.saboteur.disruption;

import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.invocation.SaboteurInvocation;
import org.cdrokar.saboteur.reader.JavaScriptReader;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Created by cdrolet on 3/4/2016.
 */
@Component
@Slf4j
public class Substitute implements Disruptive {

    public static final String DESCRIPTION = "Substitute execution result";

    public static final String KEY_CLASS_SUBSTITUTE = "substituteResultWithClass";
    public static final String KEY_SCRIPT_SUBSTITUTE = "substituteResultFromScript";
    private static final Collection<String> INSTRUCTIONS = ImmutableList.of(KEY_CLASS_SUBSTITUTE, KEY_SCRIPT_SUBSTITUTE);

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

        log.info(getDetailFrom(invocation));

        Map<String, String> instructions = getInstructionFrom(invocation);

        if (instructions.containsKey(KEY_CLASS_SUBSTITUTE)) {
            return getSubstituteFromClass(instructions.get(KEY_CLASS_SUBSTITUTE));
        }

        return getSubstituteFromScript(instructions.get(KEY_SCRIPT_SUBSTITUTE));
    }


    private Optional<Object> getSubstituteFromClass(String className) {
        if (className.equals(Void.class.getSimpleName())) {
            return Optional.empty();
        }

        try {
            return Optional.of(Class.forName(className).newInstance());
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
            throw new RuntimeException("Error occur when reading instructions for: " + getClass().getSimpleName(), ex);
        }
    }

    private Optional<Object> getSubstituteFromScript(String script) {
        return Optional.of(JavaScriptReader.INSTANCE.read(script));
    }
}
