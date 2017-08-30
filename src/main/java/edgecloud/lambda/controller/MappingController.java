package edgecloud.lambda.controller;

import edgecloud.lambda.entity.Event;
import edgecloud.lambda.entity.Function;
import edgecloud.lambda.entity.MyMapping;
import edgecloud.lambda.repository.MappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class MappingController {

    private static final Logger log = LoggerFactory.getLogger(MappingController.class);

    @Autowired
    private MappingRepository myMappingRepository;

    @GetMapping("/mappings")
    public String listMappings(Model listMap) {
        log.info("Querying mapping...");
        List<MyMapping> mappings = mappingRepository.findAll();

        for (MyMapping mapping: mappings) {
            log.info(mapping.toString());
        }

        listMap.addAttribute("mappings", mappings);
        return "mappings";
    }

    @PostMapping("/create")
    public String createMapping(@ModelAttribute Function function, @ModelAttribute Event event) throws IOException {
        log.info("Creating lambda function and event mapping: " + function.getFuncName() + event.getEventName());

        MyMapping mapping = new MyMapping();
        mapping.setId(UUID.randomUUID().toString().replace("-", ""));
        mapping.setEventId(event.getEventId());
        mapping.setEventName(event.getEventName());
        mapping.setFuncId(function.getFuncId());
        mapping.setFuncName(function.getFuncName());

        MyMapping res = myMappingRepository.save(mapping);
        log.info("lambda function and event mapping created: " + res.toString());
        return "redirect:/mappings";
    }



//    @GetMapping("/maps")
//    public String listMappings(Model map) {
//        return "mappings";
//    }
//
//    @PostMapping("/map")
//    public String mapFuncWithEvent(Model map) {
//        return "mappings";
//    }
}
