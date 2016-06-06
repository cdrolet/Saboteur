package org.cdrokar.saboteur.validation;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.domain.TargetProfile;
import org.cdrokar.saboteur.exception.ValidationException;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public enum TargetProfileValidator implements Consumer<TargetProfile> {

    INSTANCE;

    @Override
    public void accept(TargetProfile targetProfile) {

        checkAlias(targetProfile.getAlias(), targetProfile.getClassPath());

        checkClassPath(targetProfile.getAlias(), targetProfile.getClassPath(), targetProfile.isTargetSubclass());

        checkMethod(targetProfile.getClassPath(), targetProfile.getMethod());

        checkInstructions(targetProfile.getInstructions());
    }

    private void checkAlias(String alias, String classPath) {
        if (Strings.isNullOrEmpty(alias)) {
            String path = Strings.isNullOrEmpty(classPath) ? "{undefined}" : classPath;
            throw new ValidationException(ValidationException.Type.ALIAS_IS_UNDEFINED, path);
        }
    }

    private void checkClassPath(String alias, String classPath, boolean isTargettingSubclass) {
        if (Strings.isNullOrEmpty(classPath)) {
            String aliasName = Strings.isNullOrEmpty(alias) ? "{undefined}" : alias;
            throw new ValidationException(ValidationException.Type.CLASSPATH_IS_UNDEFINED, aliasName);
        }

        if (classPath.contains("*")) {
            if (isTargettingSubclass) {
                throw new ValidationException(ValidationException.Type.PARENT_CLASSPATH_SHOULD_NOT_CONTAIN_WILDCARD, classPath);
            }
            return;
        }

        if (!getClassFromPath(classPath).isPresent()) {
            throw new ValidationException(ValidationException.Type.INVALID_CLASS_PATH, classPath);
        }
    }

    private void checkInstructions(Map<String, String> instructions) {

        instructions.keySet().stream().forEach((key) -> checkInstructionKeyExist(key));

        instructions.values().stream().forEach((value) -> checkInstructionIsNotEmpty(value));
    }

    private void checkInstructionKeyExist(String key) {
        Disruptive.REGISTRY.stream()
                .filter(disruptive -> disruptive.getInstructionKeys().contains(key))
                .findAny()
                .orElseThrow(() -> new ValidationException(
                        ValidationException.Type.UNKNOWN_INSTRUCTION_KEY,
                        key,
                        Joiner.on(",").join(
                                Disruptive.REGISTRY
                                        .stream()
                                        .map(d -> d.getInstructionKeys())
                                        .collect(Collectors.toList()))));
    }

    private void checkInstructionIsNotEmpty(String value) {
        if (Strings.isNullOrEmpty(value)) {
            throw new ValidationException(ValidationException.Type.INSTRUCTION_IS_EMPTY, value);
        }
    }

    private void checkMethod(String classPath, String method) {
        if (method.equals("*") || classPath.contains("*")) {
            return;
        }

        Optional<Class<?>> clazz = getClassFromPath(classPath);
        if (!clazz.isPresent()) {
            return;
        }

        Arrays.stream(clazz.get().getMethods())
                .filter((classMethod) -> classMethod.getName().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ValidationException.Type.INVALID_METHOD, method, classPath));
    }

    private Optional<Class<?>> getClassFromPath(String classPath) {
        try {
            // Spring factory bean path started with a "&"
            return Optional.of(Class.forName(classPath.replace("&", "")));
        } catch (ClassNotFoundException ex) {
            return Optional.empty();
        }

    }

}
