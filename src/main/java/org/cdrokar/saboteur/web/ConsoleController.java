package org.cdrokar.saboteur.web;

import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

import org.cdrokar.saboteur.Repository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ConsoleController {

    private final Repository repository;

    @RequestMapping(method = RequestMethod.GET, value = "/saboteur")
    public String console(@ModelAttribute("model") ModelMap model) {

        model.addAttribute("actions",
                repository.getActions());

        return "index";
    }


}
