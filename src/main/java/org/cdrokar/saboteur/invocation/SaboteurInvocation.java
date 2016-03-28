package org.cdrokar.saboteur.invocation;

import org.cdrokar.saboteur.domain.TargetProfile;
import lombok.Getter;

import java.util.Map;

/**
 * Created by cdrolet on 3/12/2016.
 */
@Getter
public class SaboteurInvocation extends SourceInvocation {

    private final Map<String, String> instructions;

    public SaboteurInvocation(
            SourceInvocation invocation,
            Map<String, String> instructions) {
        super(invocation.getBeanDefinition(), invocation.getMethodInvocation());
        if (instructions.isEmpty()) {
            this.instructions = TargetProfile.DEFAULT.getInstructions();
        } else {
            this.instructions = instructions;
        }
    }
}
