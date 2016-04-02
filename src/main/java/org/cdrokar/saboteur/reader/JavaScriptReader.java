package org.cdrokar.saboteur.reader;


import org.cdrokar.saboteur.exception.JavaScriptException;
import org.cdrokar.saboteur.exception.ValidationException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by cdrolet on 3/12/2016.
 */

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
