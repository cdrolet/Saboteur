package org.cdrokar.saboteur.invocation;

import lombok.Getter;

import java.util.List;

import org.cdrokar.saboteur.domain.Instruction;
import org.cdrokar.saboteur.domain.Action;

@Getter
public class SaboteurInvocation extends SourceInvocation {

    private final List<Instruction> instructions;

    public SaboteurInvocation(
            SourceInvocation invocation,
            List<Instruction> instructions) {

        super(invocation.getBeanDefinition(), invocation.getMethodInvocation());

        if (instructions.isEmpty()) {
            this.instructions = Action.DEFAULT.getInstructions();
        } else {
            this.instructions = instructions;
        }
    }
}
