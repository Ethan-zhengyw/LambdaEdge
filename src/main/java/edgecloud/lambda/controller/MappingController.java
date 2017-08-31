package edgecloud.lambda.controller;

import edgecloud.lambda.entity.Event;
import edgecloud.lambda.entity.Function;
import edgecloud.lambda.entity.EventFunctionMapping;
import edgecloud.lambda.repository.EventRepository;
import edgecloud.lambda.repository.EventFunctionMappingRepository;
import edgecloud.lambda.repository.FunctionRepository;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
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
    private FunctionRepository functionRepository;

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
        return "pages/list_event_func_mappings";
    }

    @PostMapping("/create_event_func_map")
    public String createEventFunctionMapping(@ModelAttribute EventFunctionMapping efmap) throws IOException {
        Event currentEvent = eventRepository.findByEventName(efmap.getEventName());
        if (currentEvent==null) {
            log.info("Creating event and function mapping: " + efmap.getEventName() + efmap.getFuncId());

            //Save event.
            Event event = new Event();
            event.setEventName(efmap.getEventName());
            Event eventRes = eventRepository.save(event);

            //Create event and function mapping.
            Function function = functionRepository.findOne(efmap.getFuncId());
            EventFunctionMapping mapping = new EventFunctionMapping();
//        mapping.setId(UUID.randomUUID().toString().replace("-", ""));
            mapping.setEventId(eventRes.getId());
            mapping.setEventName(eventRes.getEventName());
            mapping.setFuncId(function.getId());
            mapping.setFuncName(function.getFuncName());
            EventFunctionMapping res = mappingRepository.save(mapping);
            log.info("Event and function mapping created: " + res.toString());
        }
        else{
            log.info("Event and function mapping already existed: " + currentEvent.toString());
        }
        return "redirect:/list_event_func_mappings";
    }
}
