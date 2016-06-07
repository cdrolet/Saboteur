package org.cdrokar.saboteur.infiltration;

import org.cdrokar.saboteur.SaboteurRepository;
import org.cdrokar.saboteur.domain.BeanDefinition;
import org.cdrokar.saboteur.invocation.InvocationWorkflowSupplier;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("serial")
public class TargetProxyBeanCreator extends AbstractAutoProxyCreator {


    @Autowired
    private SaboteurRepository repository;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource targetSource) {

        if (beanClass.getPackage() != null
                && beanClass.getPackage().getName().startsWith("org.springframework.boot")) {
            return DO_NOT_PROXY;
        }

        BeanDefinition beanDefinition = new BeanDefinition(beanClass, beanName);

        if (beanDefinition.isSaboteurBean()) {
            return DO_NOT_PROXY;
        }

        if (!repository.isBeanInfiltrated(beanDefinition)) {
            return DO_NOT_PROXY;
        }

        return getAdviceFor(beanDefinition);
    }

    private Object[] getAdviceFor(BeanDefinition beanDefinition) {

        return new Object[]{
                new TargetMethodInterceptor(
                        beanDefinition,
                        new InvocationWorkflowSupplier(repository, beanDefinition))};
    }




}


