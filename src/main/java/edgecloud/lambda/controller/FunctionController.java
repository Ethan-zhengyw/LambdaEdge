package edgecloud.lambda.controller;

import edgecloud.lambda.entity.Function;
import edgecloud.lambda.repository.FunctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FunctionController {

    private static final Logger log = LoggerFactory.getLogger(FunctionController.class);

    @Autowired
    private FunctionRepository functionRepository;

    @GetMapping("/functions")
    public String listFunctions(Model map) {
        return "functions";
    }

    @PostMapping("/create")
    public String createFunction(@ModelAttribute Function function) {

        log.info("Creating lambda function: " + function.toString());
        // TODO
        // 1. call api to create function
        // 2. save function to repository
        functionRepository.save(function);

        return "functions";
    }

    @PostMapping("/push")
    public String pushFunction(@ModelAttribute Function function) {

        log.info("Creating lambda function: " + function.toString());
        // TODO
        // 1. call api to create function
        // 2. save function to repository
        functionRepository.save(function);

        return "functions";
    }

}
