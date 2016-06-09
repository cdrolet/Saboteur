package org.cdrokar.saboteur.validation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.domain.Action;
import org.cdrokar.saboteur.domain.Instruction;
import org.cdrokar.saboteur.exception.ValidationException;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public enum ActionValidator implements Consumer<Action> {

    INSTANCE;

    @Override
    public void accept(Action action) {

        checkAlias(action.getName(), action.getTargetClassPath());

        checkClassPath(action.getName(), action.getTargetClassPath(), action.isWithSubclass());

        checkMethod(action.getName(), action.getTargetClassPath(), action.getMethod());

        checkInstructions(action.getInstructions());
    }

    private void checkAlias(String name, String classPath) {
        if (Strings.isNullOrEmpty(name)) {
            String path = Strings.isNullOrEmpty(classPath) ? "{classpath undefined}" : classPath;
            throw new ValidationException(ValidationException.Type.NAME_IS_UNDEFINED, path);
        }
    }

    private void checkClassPath(String name, String classPath, boolean isTargettingSubclass) {
        if (Strings.isNullOrEmpty(classPath)) {
            String sourceName = Strings.isNullOrEmpty(name) ? "{name undefined}" : name;
            throw new ValidationException(ValidationException.Type.CLASSPATH_IS_UNDEFINED, sourceName);
        }

        if (classPath.contains("*")) {
            if (isTargettingSubclass) {
                throw new ValidationException(ValidationException.Type.PARENT_CLASSPATH_SHOULD_NOT_CONTAIN_WILDCARD, name, classPath);
            }
            return;
        }

        if (!getClassFromPath(classPath).isPresent()) {
            throw new ValidationException(ValidationException.Type.INVALID_CLASS_PATH, name, classPath);
        }
    }

    private void checkInstructions(List<Instruction> instructions) {

        instructions.stream().forEach(
                (instruction) -> checkInstruction(instruction)
        );
    }

    private void checkInstruction(Instruction instruction) {
        checkInstructionKeyExist(instruction.getKey());
        checkInstructionIsNotEmpty(instruction.getValue());
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

    private void checkMethod(String name, String classPath, String method) {
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
                .orElseThrow(() -> new ValidationException(ValidationException.Type.INVALID_METHOD, name, method, classPath));
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
