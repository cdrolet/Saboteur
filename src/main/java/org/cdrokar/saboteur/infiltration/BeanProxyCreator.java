package org.cdrokar.saboteur.infiltration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

import org.cdrokar.saboteur.Repository;
import org.cdrokar.saboteur.invocation.InvocationWorkflowSupplier;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("serial")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BeanProxyCreator extends AbstractAutoProxyCreator {

    private final Repository repository;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource targetSource) {

        BeanDefinition beanDefinition = new BeanDefinition(beanClass, beanName);

        if (beanDefinition.isSaboteurBean()) {
            return DO_NOT_PROXY;
        }

        if (!repository.isAnyActionMatchBean(beanDefinition)) {
            return DO_NOT_PROXY;
        }

        return getAdviceFor(beanDefinition);
    }

    @Override
    protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
        try {
            return super.wrapIfNecessary(bean, beanName, cacheKey);
        } catch (BeanCreationException | AopConfigException ex) {
            //TODO add rejected target to the repository
            log.warn("unable to target: " + beanName);
            return bean;
        }
    }

    private Object[] getAdviceFor(BeanDefinition beanDefinition) {

        return new Object[]{
                new BeanMethodInterceptor(
                        beanDefinition,
                        new InvocationWorkflowSupplier(repository, beanDefinition))};
    }

}


