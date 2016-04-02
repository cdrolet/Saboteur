package org.cdrokar.saboteur.disruption;

import org.cdrokar.saboteur.invocation.SaboteurInvocation;
import org.cdrokar.saboteur.reader.JavaScriptReader;
import org.cdrokar.saboteur.disruption.Disruptive;
import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Created by cdrolet on 3/12/2016.
 */
@Component
@Slf4j
public class Execute implements Disruptive {

    public static final String DESCRIPTION = "Execute a script";

    public static final String KEY_BEFORE_EXECUTION_SCRIPT = "beforeExecutionScript";

    public static final String KEY_AFTER_EXECUTION_SCRIPT = "afterExecutionScript";

    private static final Collection<String> INSTRUCTIONS = ImmutableList.of(
            KEY_BEFORE_EXECUTION_SCRIPT,
            KEY_AFTER_EXECUTION_SCRIPT);

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Collection<String> getInstructionKeys() {
        return INSTRUCTIONS;
    }

    @Override
    @SneakyThrows
    public Optional<Object> apply(SaboteurInvocation invocation) {
        log.info(getDetailFrom(invocation));

        Map<String, String> instructions = getInstructionFrom(invocation);

        executeIfKeyPresent(instructions, KEY_BEFORE_EXECUTION_SCRIPT);

        Optional<Object> result = Optional.of(invocation.proceed());

        executeIfKeyPresent(instructions, KEY_AFTER_EXECUTION_SCRIPT);

        return result;
    }

    private void executeIfKeyPresent(Map<String, String> instructions, String key) {
        if (!instructions.containsKey(key)) {
            return;
        }

        JavaScriptReader.INSTANCE.read(instructions.get(KEY_BEFORE_EXECUTION_SCRIPT));
    }

}
