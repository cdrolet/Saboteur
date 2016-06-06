package org.cdrokar.saboteur.domain;

import lombok.Data;

import org.cdrokar.saboteur.SaboteurRepository;

@Data
public class BeanDefinition {

    private final Class<?> beanClass;
    private final String beanName;

    public String getClassName() {
        return beanClass.getName();
    }

    public boolean isSaboteurBean() {
        return beanClass.getPackage() != null
                && beanClass.getPackage().getName().startsWith(SaboteurRepository.class.getPackage().getName());
    }
}
