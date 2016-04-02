package org.cdrokar.saboteur;

import lombok.extern.slf4j.Slf4j;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.domain.Configuration;
import org.cdrokar.saboteur.domain.TargetProfile;
import org.cdrokar.saboteur.exception.JavaScriptException;
import org.cdrokar.saboteur.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/saboteur", produces = "application/json")
public class SaboteurController {

    private final SaboteurRepository repository;

    @Autowired
    public SaboteurController(SaboteurRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Configuration getConfiguration() {

        return repository.getConfiguration();
    }

    @RequestMapping(value = "/disruptives", method = RequestMethod.GET)
    public Collection<Disruptive> getDisruptives() {

        return Disruptive.REGISTRY;
    }

    @RequestMapping(value = "/targets", method = RequestMethod.GET)
    public Collection<TargetProfile> getTargets() {

        return repository.getTargets();
    }

    @RequestMapping(value = "/targets/{targetName}", method = RequestMethod.GET)
    public TargetProfile getTarget(
            @PathVariable String targetName) {

        return repository.getTarget(targetName);
    }

    @RequestMapping(value = "/targets/{targetName}/path", method = RequestMethod.GET)
    public String getTargetPath(
            @PathVariable String targetName) {

        return repository.getTarget(targetName).getClassPath();
    }

    @RequestMapping(value = "/targets/{targetName}/disrupted", method = RequestMethod.GET)
    public Boolean getTargetState(
            @PathVariable String targetName) {

        return repository.getTarget(targetName).isDisrupted();
    }

    @RequestMapping(value = "/targets/{targetName}/method", method = RequestMethod.GET)
    public String getTargetMethod(
            @PathVariable String targetName) {

        return repository.getTarget(targetName).getMethod();
    }

    @RequestMapping(value = "/targets/{targetName}/instructions", method = RequestMethod.GET)
    public Map<String, String> getTargetInstructions(
            @PathVariable String targetName) {

        return repository.getTarget(targetName).getInstructions();
    }

    @RequestMapping(value = "/targets/{targetName}/instructions", method = RequestMethod.PUT)
    public ResponseEntity setTargetInstructions(
            @PathVariable String targetName,
            @PathVariable Map<String, String> map) {

        repository.getTarget(targetName).setInstructions(map);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/targets/{targetName}/instructions/{key}", method = RequestMethod.GET)
    public String getTargetInstruction(
            @PathVariable String targetName,
            @PathVariable String key) {

        return repository.getTarget(targetName).getInstruction(key);
    }

    @RequestMapping(value = "/targets/{targetName}/instructions/{key}", method = RequestMethod.PUT)
    public ResponseEntity setTargetInstruction(
            @PathVariable String targetName,
            @PathVariable String key,
            @RequestBody String value) {

        repository.getTarget(targetName).setInstruction(key, value);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException ex) {
        log.error(ex.getMessage(), ex);
        return ex.toMap();
    }

    @ExceptionHandler(JavaScriptException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, Map<String, String>> handleValidationException(JavaScriptException ex) {
        log.error(ex.getMessage(), ex);
        return ex.toMap();
    }
}
