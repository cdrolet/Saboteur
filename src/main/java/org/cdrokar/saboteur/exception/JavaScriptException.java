package org.cdrokar.saboteur.exception;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import javax.script.ScriptException;

import com.google.common.collect.ImmutableMap;

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
