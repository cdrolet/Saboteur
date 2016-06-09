package org.cdrokar.saboteur.invocation;

import lombok.Data;

import org.aopalliance.intercept.MethodInvocation;
import org.cdrokar.saboteur.infiltration.BeanDefinition;

@Data
public class SourceInvocation {

    private final BeanDefinition beanDefinition;

    private final MethodInvocation methodInvocation;

    public Object proceed() throws Throwable {
        return methodInvocation.proceed();
    }
}
