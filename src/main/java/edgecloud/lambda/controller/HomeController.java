package edgecloud.lambda.controller;

import edgecloud.lambda.entity.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    // home page
    @GetMapping("/")
    public String index(Model map) {
        return "index";
    }


    // Operate Page
    @GetMapping("/create")
    public String creatFunction(Model map) {
        map.addAttribute("function", new Function());
        return "create_function";
    }

    @GetMapping("/push")
    public String pushFunction(Model map) {
        return "push_function";
    }

    @GetMapping("/map")
    public String mapFuncWithEvent(Model map) {
        return "mapping";
    }

    @GetMapping("/send")
    public String sendEvent(Model map) {
        return "send_event";
    }
}
