package org.cdrokar.saboteur.disruption;


import org.cdrokar.saboteur.SaboteurRepository;
import org.cdrokar.saboteur.SaboteurRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by cdrolet on 3/4/2016.
 */


@Component
public class DisruptiveBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    SaboteurRepository repository;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Disruptive) {
            repository.addDisruptive((Disruptive) bean);
        }
        return bean;
    }

}
