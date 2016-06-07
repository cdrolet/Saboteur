package org.cdrokar.saboteur.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.cdrokar.saboteur.disruption.Throw;
import org.cdrokar.saboteur.exception.SabotageException;

import com.typesafe.config.Config;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Instruction {

    public static final Instruction DEFAULT = new Instruction(Throw.KEY_BEFORE_EXCEPTION_CLASS, SabotageException.class.getName());

    private String key;
    private String value;

    public static Instruction from(Config config) {
        return new Instruction(
                config.getString("key"),
                config.getString("value"));
    }
}
