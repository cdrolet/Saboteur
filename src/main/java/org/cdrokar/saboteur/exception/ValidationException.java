package org.cdrokar.saboteur.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by cdrolet on 3/30/2016.
 */
@Getter
public class ValidationException extends RuntimeException {

    @RequiredArgsConstructor
    public enum Type {

        INVALID_CLASS_PATH("The classpath %s is invalid"),
        INVALID_METHOD("The method %s is not part of class %s"),
        TARGET_NOT_FOUND("%s is not part of the saboteur known targets"),
        INSTRUCTION_KEY_ALREADY_DEFINED("Instruction key %s from component %s is already defined in component %s"),
        INSTRUCTION_NOT_FOUND("Instruction key %s is not part of %s instructions"),
        INSTRUCTION_IS_EMPTY("Instruction is empty for key %s"),
        UNKNOWN_INSTRUCTION_KEY("Instruction key %s is unknown, valid instructions keys are: %s");

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


}
