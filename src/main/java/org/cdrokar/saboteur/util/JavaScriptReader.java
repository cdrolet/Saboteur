package org.cdrokar.saboteur.util;

import lombok.SneakyThrows;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by cdrolet on 3/12/2016.
 */

public enum JavaScriptReader {

    INSTANCE;

    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    @SneakyThrows
    public Object read(String script) {
        return engine.eval(script);
    }


}
