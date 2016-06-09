package org.cdrokar.saboteur.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

@Getter
public class ValidationException extends RuntimeException {

    @RequiredArgsConstructor
    public enum Type {

        INVALID_CLASS_PATH("For action '%s', target classpath '%s' is invalid"),
        INVALID_METHOD("For action '%s' method '%s' is not part of class '%s'"),
        ACTION_NOT_FOUND("'%s' is not part of the saboteur known actions"),
        INSTRUCTION_KEY_ALREADY_DEFINED("Instruction key '%s' from disruptive '%s' is already defined in disruptive '%s'"),
        INSTRUCTION_NOT_FOUND("Instruction key '%s' is not part of '%s' instructions"),
        INSTRUCTION_IS_EMPTY("Instruction is empty for key '%s'"),
        CONFIGURATION_FILE_IS_MISSING("File '%s' can't be located in the classpath"),
        NAME_IS_UNDEFINED("Name for profile classpath '%s' is undefined"),
        CLASSPATH_IS_UNDEFINED("Classpath for action '%s' is undefined"),
        UNKNOWN_INSTRUCTION_KEY("Instruction key '%s' is unknown. The valid instructions keys are: '%s'"),
        PARENT_CLASSPATH_SHOULD_NOT_CONTAIN_WILDCARD("For Action '%s', target classpath '%s' is invalid. Classpath can't contain wildcard when using subclass");

        private final String message;

        public String getMessage(String... arg) {
            return String.format(message, arg);
        }
    }

    private final Type type;
    private final String message;

    public ValidationException(Type type, String... arg) {
        this.type = type;
        message = type.getMessage(arg);
    }

    public Map<String, String> toMap() {
        return ImmutableMap.of(type.name(), message);
    }
}
