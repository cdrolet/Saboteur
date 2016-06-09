package org.cdrokar.saboteur.disruption;


import org.cdrokar.saboteur.validation.DisruptiveValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class DisruptiveProcessor implements BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Disruptive) {
            DisruptiveValidator.INSTANCE.accept((Disruptive) bean);
            Disruptive.REGISTRY.add((Disruptive) bean);
        }
        return bean;
    }

}
