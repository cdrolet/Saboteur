package org.cdrokar.saboteur.domain;

import org.cdrokar.saboteur.SaboteurRepository;
import lombok.Data;

/**
 * Created by cdrolet on 3/27/2016.
 */
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
