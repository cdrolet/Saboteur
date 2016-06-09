package org.cdrokar.saboteur.infiltration;

import lombok.Data;

import org.cdrokar.saboteur.SaboteurConfiguration;

@Data
public class BeanDefinition {

    private final Class<?> beanClass;
    private final String beanName;

    public String getClassName() {
        return beanClass.getName();
    }

    public boolean isSaboteurBean() {
        return beanClass.getPackage() != null
                && beanClass.getPackage().getName().startsWith(SaboteurConfiguration.class.getPackage().getName());
    }
}
