package org.cdrokar.saboteur.exception;

/**
 * Created by cdrolet on 3/25/2016.
 */

public class TargetNotFoundException extends RuntimeException {

    private static final String MESSAGE = "%s is not part of the saboteur known targets";

    public TargetNotFoundException(String targetName) {
        super(String.format(MESSAGE, targetName));
    }


}
