package edgecloud.lambda.controller;

import com.sun.org.apache.xpath.internal.functions.FuncId;
import edgecloud.lambda.entity.Function;
import edgecloud.lambda.repository.FunctionRepository;
import edgecloud.lambda.utils.FunctionIO;
import edgecloud.lambda.utils.GetConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Controller
public class FunctionController {

    private static final Logger log = LoggerFactory.getLogger(FunctionController.class);

    @Autowired
    private FunctionRepository functionRepository;

    @GetMapping("/functions")
    public String listFunctions(Model map) {
        log.info("Querying function...");
        List<Function> functions = functionRepository.findAll();

        for (Function function : functions) {
            log.info(function.toString());
        }

        map.addAttribute("functions", functions);
        return "functions";
    }

    @PostMapping("/create")
    public String createFunction(@ModelAttribute Function function) throws IOException {
        log.info("Creating lambda function: " + function.toString());

        log.info("Generating storage path for function...");
        String funcDir = new GetConfig().readConfig("application.properties")
                .getProperty("edgecloud.lambda.functionLocation");
        String funcPath = String.format("%s/%s/%s", funcDir, function.getFuncName(), function.getFuncVersion());
        log.info("Function storage path is: " + funcPath);
        function.setFuncPath(funcPath);

        // TODO
        // 1. call api to create function ? No need ?
        // 2. save function to repository
        Function res = functionRepository.save(function);
        log.info("lambda function created: " + res.toString());

        FunctionIO.writeFunction(res.getFuncBody(), res.getFuncPath());

        return "redirect:/functions";
    }

    @PostMapping("/push")
    public String pushFunction(@ModelAttribute Function function) {

        log.info("Pushing lambda function to node: " + function.toString());
        // TODO
        // 1. call api to push
        // 2. save function-node map to repository
        functionRepository.save(function);

        return "redirect:/functions";
    }

}
