package org.cdrokar.saboteur.infiltration;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.function.BiPredicate;

import org.cdrokar.saboteur.domain.Action;
import org.springframework.util.PatternMatchUtils;


public enum BeanActionMatcher implements BiPredicate<BeanDefinition, Action> {

    INSTANCE;

    @RequiredArgsConstructor
    public enum MatchType {
        CLASS((beanDefinition, action) -> PatternMatchUtils.simpleMatch(action.getTargetClassPath(), beanDefinition.getClassName())),
        PARENT((beanDefinition, action) -> {
            if (!action.isWithSubclass()) {
                return false;
            }
            try {
                Class<?> targetClass = Class.forName(action.getTargetClassPath());
                if (targetClass.isAssignableFrom(beanDefinition.getBeanClass())) {
                    return true;
                }
            } catch (ClassNotFoundException e) {
                return false;
            }
            return false;
        });

        private final BiPredicate<BeanDefinition, Action> predicate;
    }

    @Override
    public boolean test(BeanDefinition beanDefinition, Action action) {
        return Arrays.stream(MatchType.values()).anyMatch(type -> type.predicate.test(beanDefinition, action));
    }


}
