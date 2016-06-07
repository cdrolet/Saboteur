package org.cdrokar.saboteur.reader;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.cdrokar.saboteur.exception.JavaScriptException;


public enum JavaScriptReader {

    INSTANCE;

    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    public Object read(String script) {
        try {
            return engine.eval(script);
        } catch (ScriptException e) {
            throw new JavaScriptException(e, script);
        }
    }


}
