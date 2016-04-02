package org.cdrokar.saboteur.exception;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;

import javax.script.ScriptException;
import java.util.Map;

/**
 * Created by cdrolet on 4/1/2016.
 */
@RequiredArgsConstructor
public class JavaScriptException extends RuntimeException{

    private final ScriptException exception;
    private final String script;

    public Map<String, Map<String, String>> toMap() {
        return ImmutableMap.of(
                JavaScriptException.class.getSimpleName(),
                ImmutableMap.of(
                        "script", script,
                        "line number", String.valueOf(exception.getLineNumber()),
                        "column number", String.valueOf(exception.getColumnNumber()),
                        "message", exception.getMessage()));
    }

}
