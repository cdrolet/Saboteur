package org.cdrokar.saboteur.exception;

/**
 * Created by cdrolet on 3/25/2016.
 */

public class InstructionNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Instruction key %s is not part of %s instructions";

    public InstructionNotFoundException(String targetName, String key) {
        super(String.format(MESSAGE, targetName, key));
    }


}
