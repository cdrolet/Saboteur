package org.cdrokar.saboteur.web;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.websocket.server.PathParam;

import org.cdrokar.saboteur.SaboteurRepository;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.domain.Instruction;
import org.cdrokar.saboteur.domain.TargetProfile;
import org.cdrokar.saboteur.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/saboteur", produces = "application/json")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SaboteurController {

    private final SaboteurRepository repository;

    @RequestMapping(method = RequestMethod.GET, value = "/disruptives")
    public Collection<Disruptive> getDisruptives() {
        return Disruptive.REGISTRY;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/targets")
    public Collection<TargetProfile> getProfiles() {
        return repository.getTargets();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/targets")
    public void getProfiles(Collection<TargetProfile> targets) {
        repository.saveTargets(targets);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/target/{name}")
    public TargetProfile getProfile(@PathParam("name") String name) {
        return repository.getTargetProfile(name);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/target")
    public void saveProfile(@RequestBody TargetProfile targets) {
        repository.saveTarget(targets);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/target/{name}/disrupt/{value}")
    public void setDisrupted(@PathParam("name") String name, @PathParam("value") String value) {
        repository.getTargetProfile(name).setDisrupted(Boolean.valueOf(value));
        repository.updateVersion();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/target/{name}/instructions")
    public void setDisrupted(@PathParam("name") String name, @RequestBody List<Instruction> instructions) {
        repository.getTargetProfile(name).setInstructions(instructions);
        repository.updateVersion();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ValidationException handleValidationException(ValidationException ex) {
        return ex;
    }
}
