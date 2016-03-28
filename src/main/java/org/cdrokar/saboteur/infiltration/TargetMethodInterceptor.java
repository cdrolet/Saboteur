package org.cdrokar.saboteur.infiltration;

import org.cdrokar.saboteur.domain.BeanDefinition;
import org.cdrokar.saboteur.domain.BeanDefinition;
import org.cdrokar.saboteur.invocation.InvocationWorkflow;
import org.cdrokar.saboteur.invocation.SourceInvocation;
import lombok.Data;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.function.Supplier;

@Data
public class TargetMethodInterceptor implements MethodInterceptor {

    private final BeanDefinition beanDefinition;
    private final Supplier<InvocationWorkflow> supplier;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return supplier.get().invoke(new SourceInvocation(beanDefinition, methodInvocation));
    }

}
