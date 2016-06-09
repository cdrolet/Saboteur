package org.cdrokar.saboteur.infiltration;

import lombok.Data;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.cdrokar.saboteur.invocation.InvocationWorkflowSupplier;
import org.cdrokar.saboteur.invocation.SourceInvocation;

@Data
public class BeanMethodInterceptor implements MethodInterceptor {

    private final BeanDefinition beanDefinition;
    private final InvocationWorkflowSupplier supplier;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return supplier.get().invoke(new SourceInvocation(beanDefinition, methodInvocation));
    }

}
