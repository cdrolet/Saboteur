package org.cdrokar.saboteur.invocation;

import org.cdrokar.saboteur.domain.BeanDefinition;
import org.cdrokar.saboteur.domain.BeanDefinition;
import lombok.Data;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Created by cdrolet on 3/4/2016.
 */
@Data
public class SourceInvocation {

    private final BeanDefinition beanDefinition;

    private final MethodInvocation methodInvocation;

    public Object proceed() throws Throwable {
        return methodInvocation.proceed();
    }
}
