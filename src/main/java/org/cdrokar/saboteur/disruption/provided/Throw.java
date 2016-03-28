package org.cdrokar.saboteur.disruption.provided;

import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.invocation.SaboteurInvocation;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.invocation.SaboteurInvocation;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
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
public class Throw implements Disruptive {

    public static final String DESCRIPTION = "Throw a runtime exception";

    public static final String KEY_BEFORE_EXCEPTION_CLASS = "throwExceptionBefore";

    public static final String KEY_AFTER_EXCEPTION_CLASS = "throwExceptionAfter";

    public static final String KEY_EXCEPTION_MESSAGE = "exceptionMessage";

    private static final Collection<String> INSTRUCTIONS = ImmutableList.of(
            KEY_BEFORE_EXCEPTION_CLASS, KEY_AFTER_EXCEPTION_CLASS, KEY_EXCEPTION_MESSAGE);

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

        String className = getClassNameFrom(instructions);

        String message = getMessageFrom(instructions, invocation);

        if (isBefore(instructions)) {
            throwExceptionFrom(className, message);
        }

        proceed(invocation);

        throwExceptionFrom(className, message);

        // Unreachable
        return Optional.empty();
    }

    private String getClassNameFrom(Map<String, String> instructions) {
        String className = instructions.get(KEY_BEFORE_EXCEPTION_CLASS);
        if (Strings.isNullOrEmpty(className)) {
            className = instructions.get(KEY_AFTER_EXCEPTION_CLASS);
        }
        if (Strings.isNullOrEmpty(className)) {
            throw new IllegalStateException("no instructions defined for  " + getClass().getSimpleName());
        }
        return className;
    }

    private String getMessageFrom(Map<String, String> instructions, SaboteurInvocation invocation) {
        String message = instructions.get(KEY_EXCEPTION_MESSAGE);
        if (Strings.isNullOrEmpty(message)) {
            return getDetailFrom(invocation);
        }
        return message;
    }

    private boolean isBefore(Map<String, String> instructions) {
        return instructions.containsKey(KEY_BEFORE_EXCEPTION_CLASS);
    }

    private void throwExceptionFrom(String className, String message) {
        RuntimeException result;

        try {
            result = (RuntimeException) Class.forName(className).getConstructor(String.class).newInstance(message);
        } catch (Exception ex) {
            result = new IllegalStateException("Error occur when creating throwable instance for class: " + className, ex);
        }

        throw result;

    }

    @SneakyThrows
    private void proceed(SaboteurInvocation invocation) {
        invocation.getMethodInvocation().proceed();
    }
}
