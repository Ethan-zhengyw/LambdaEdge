package edgecloud.lambda.controller;

import edgecloud.lambda.entity.Event;
import edgecloud.lambda.entity.Function;
import edgecloud.lambda.entity.EventFunctionMapping;
import edgecloud.lambda.repository.EventRepository;
import edgecloud.lambda.repository.EventFunctionMappingRepository;
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
//import java.util.UUID;

@Controller
public class MappingController {

    private static final Logger log = LoggerFactory.getLogger(MappingController.class);

    @Autowired
    private EventFunctionMappingRepository mappingRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/list_event_func_mappings")
    public String listEventFuncMappings(Model Map) {
        log.info("Querying mappings...");
        List<EventFunctionMapping> mappings = mappingRepository.findAll();

        for (EventFunctionMapping mapping: mappings) {
            log.info(mapping.toString());
        }

        Map.addAttribute("list_event_func_mappings", mappings);
        return "list_event_func_mappings";
    }

    @PostMapping("/create_event_func_map")
    public String createEventFunctionMapping(@ModelAttribute Event event, @ModelAttribute Function function) throws IOException {
        log.info("Creating event and function mapping: " + function.getFuncName() + event.getEventName());

        EventFunctionMapping mapping = new EventFunctionMapping();
//        mapping.setId(UUID.randomUUID().toString().replace("-", ""));
        mapping.setEventId(event.getId());
        mapping.setEventName(event.getEventName());
        mapping.setFuncId(function.getId());
        mapping.setFuncName(function.getFuncName());

        Event eventRes = eventRepository.save(event);
        EventFunctionMapping res = mappingRepository.save(mapping);
        log.info("Event and function mapping created: " + res.toString());
        return "redirect:/event_func_mappings";
    }
}
