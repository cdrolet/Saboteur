package org.cdrokar.saboteur.web;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

import javax.inject.Inject;
import javax.websocket.server.PathParam;

import org.cdrokar.saboteur.SaboteurRepository;
import org.cdrokar.saboteur.disruption.Disruptive;
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

    @RequestMapping(method = RequestMethod.POST, value = "/profile")
    public void saveProfile(@RequestBody TargetProfile profile) {
        repository.saveProfile(profile);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/profile/{name}")
    public TargetProfile getProfile(@PathParam("name") String name) {

        return repository.getTargetProfile(name);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ValidationException handleValidationException(ValidationException ex) {
        return ex;
    }
}
