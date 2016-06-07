package org.cdrokar.saboteur.web;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

import javax.inject.Inject;

import org.cdrokar.saboteur.SaboteurRepository;
import org.cdrokar.saboteur.disruption.Disruptive;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

}
