package org.cdrokar.saboteur.web;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.websocket.server.PathParam;

import org.cdrokar.saboteur.Repository;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.cdrokar.saboteur.domain.Instruction;
import org.cdrokar.saboteur.domain.Action;
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
public class ActionController {

    private final Repository repository;

    @RequestMapping(method = RequestMethod.GET, value = "/disruptives")
    public Collection<Disruptive> getDisruptives() {
        return Disruptive.REGISTRY;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/actions")
    public Collection<Action> getActions() {
        return repository.getActions();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/actions")
    public void setActions(@RequestBody List<Action> actions) {
        repository.saveActions(actions);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/action/{name}")
    public Action getAction(@PathParam("name") String name) {
        return repository.getAction(name);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/action")
    public void setActions(@RequestBody Action action) {
        repository.saveAction(action);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/action/{name}/active/{value}")
    public void setDisrupted(@PathParam("name") String name, @PathParam("value") String value) {
        repository.setActive(name, Boolean.valueOf(value));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/action/{name}/instructions")
    public void setDisrupted(@PathParam("name") String name, @RequestBody List<Instruction> instructions) {
        repository.setInstructions(name, instructions);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ValidationException handleValidationException(ValidationException ex) {
        return ex;
    }
}
