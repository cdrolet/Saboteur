package org.cdrokar.saboteur.infiltration;

import org.cdrokar.saboteur.domain.BeanDefinition;
import org.cdrokar.saboteur.domain.BeanDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.util.PatternMatchUtils;

import java.util.Arrays;
import java.util.function.BiPredicate;

/**
 * Created by cdrolet on 3/27/2016.
 */
public enum TargetMatchPredicate implements BiPredicate<BeanDefinition, String> {

    INSTANCE;

    @RequiredArgsConstructor
    public enum MatchType {
        NAME((beanDefinition, profile) -> PatternMatchUtils.simpleMatch(profile, beanDefinition.getBeanName())),
        CLASS((beanDefinition, profile) -> PatternMatchUtils.simpleMatch(profile, beanDefinition.getClassName())),
        PARENT((beanDefinition, profile) -> {
            try {
                Class<?> targetClass = Class.forName(profile);
                if (targetClass.isAssignableFrom(beanDefinition.getBeanClass())) {
                    return true;
                }
            } catch (ClassNotFoundException e) {
                return false;
            }
            return false;
        });

        private final BiPredicate<BeanDefinition, String> predicate;
    }

    private static final BiPredicate DELEGATE = Arrays.stream(MatchType.values())
            .map(matchType -> matchType.predicate)
            .reduce((previous, current) -> previous = previous.or(current))
            .get();

    @Override
    public boolean test(BeanDefinition beanDefinition, String profile) {
        return DELEGATE.test(beanDefinition, profile);
    }


}
