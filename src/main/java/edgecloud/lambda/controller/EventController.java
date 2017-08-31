
package edgecloud.lambda.controller;

import edgecloud.deviceserver.ServerAPI;
import edgecloud.lambda.entity.Event;
import edgecloud.lambda.entity.EventFunctionMapping;
import edgecloud.lambda.entity.Function;

import edgecloud.lambda.entity.FunctionNodeMap;
import edgecloud.lambda.repository.EventRepository;
import edgecloud.lambda.repository.EventFunctionMappingRepository;
import edgecloud.lambda.repository.FunctionNodeMapRepository;
import edgecloud.lambda.repository.FunctionRepository;
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

@Controller
public class EventController {
    ServerAPI serverAPI = new ServerAPI();

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventFunctionMappingRepository efMappingRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private FunctionNodeMapRepository fnMapRepository;

    @GetMapping("/list_event_results")
    public String listEvents(Model map) {
        log.info("Querying events...");
        List<Event> events = eventRepository.findAll();

        for (Event event : events) {
            log.info(event.toString());
        }

        map.addAttribute("events", events);
        return "pages/list_event_results";
    }

//    @PostMapping("/create_event")
//    public String createEvent(@ModelAttribute Event event) throws IOException {
//        log.info("Creating event: " + event.getEventName());
//
//        Event res = eventRepository.save(event);
//        log.info("Event created: " + res.toString());
//
//        return "redirect:/events";
//    }

    @PostMapping("/send_event")
    public String sendEvent(@ModelAttribute Event event) {

        log.info("Sending event: " + event.toString());

        List<EventFunctionMapping> efmaps = efMappingRepository.findByEventName(event.getEventName());

        String eventResult = "";
        for(EventFunctionMapping efmap : efmaps) {
            Integer funcId = efmap.getFuncId();
            Function currentFunction = functionRepository.findById(funcId);
            String currentFuncName = efmap.getFuncName();
            Integer currentFuncVerion = currentFunction.getFuncVersion();
            List<FunctionNodeMap> fnmaps = fnMapRepository.findByFuncId(funcId);
            for(FunctionNodeMap fnmap : fnmaps) {
                String nodeId = fnmap.getNodeId().toString();
                //TODO
                String content = String.format("{\"funcName\": \"%s\", \"funcVersion\": \"%s\", \"funcHandler\": \"%s\"}",
                        currentFuncName, currentFuncVerion, currentFunction.getFuncHandler());
                try {
                    eventResult = serverAPI.sendMessage(2, nodeId, content);
                    event.setEventResult(eventResult);
                } catch (Exception e) {
                    log.info("Send event failed." + efmap.toString());
                }
            }
        }

        return "redirect:/list_event_results";
    }
}
