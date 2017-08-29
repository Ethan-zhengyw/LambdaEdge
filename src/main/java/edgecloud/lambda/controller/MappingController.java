package edgecloud.lambda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MappingController {

    @GetMapping("/maps")
    public String listMappings(Model map) {
        return "mappings";
    }

    @PostMapping("/map")
    public String mapFuncWithEvent(Model map) {
        return "mappings";
    }
}
