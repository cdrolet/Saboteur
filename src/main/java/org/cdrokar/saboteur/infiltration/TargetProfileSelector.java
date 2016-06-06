package org.cdrokar.saboteur.infiltration;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.function.BiPredicate;

import org.cdrokar.saboteur.domain.BeanDefinition;
import org.cdrokar.saboteur.domain.TargetProfile;
import org.springframework.util.PatternMatchUtils;

public enum TargetProfileSelector implements BiPredicate<BeanDefinition, TargetProfile> {

    INSTANCE;

    @RequiredArgsConstructor
    public enum MatchType {
        NAME((beanDefinition, profile) -> PatternMatchUtils.simpleMatch(profile.getAlias(), beanDefinition.getBeanName())),
        CLASS((beanDefinition, profile) -> PatternMatchUtils.simpleMatch(profile.getClassPath(), beanDefinition.getClassName())),
        PARENT((beanDefinition, profile) -> {
            if (!profile.isTargetSubclass()) {
                return false;
            }
            try {
                Class<?> targetClass = Class.forName(profile.getClassPath());
                if (targetClass.isAssignableFrom(beanDefinition.getBeanClass())) {
                    return true;
                }
            } catch (ClassNotFoundException e) {
                return false;
            }
            return false;
        });

        private final BiPredicate<BeanDefinition, TargetProfile> predicate;
    }

    @Override
    public boolean test(BeanDefinition beanDefinition, TargetProfile targetProfile) {
        return Arrays.stream(MatchType.values()).anyMatch(type -> type.predicate.test(beanDefinition, targetProfile));
    }


}
