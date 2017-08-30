package edgecloud.lambda.controller;


import edgecloud.lambda.entity.Event;
import edgecloud.lambda.entity.EventFunctionMapping;
import edgecloud.lambda.entity.Function;

import edgecloud.lambda.repository.EventRepository;
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
    ServerAPI  serverAPI = new ServerAPI();

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/events")
    public String listEvents(Model map) {
        log.info("Querying events...");
        List<Event> events = eventRepository.findAll();

        for (Event event : events) {
            log.info(event.toString());
        }

        map.addAttribute("events", events);
        return "events";
    }

    @PostMapping("/create_event")
    public String createEvent(@ModelAttribute Event event) throws IOException {
        log.info("Creating event: " + event.getEventName());

        Event res = eventRepository.save(event);
        log.info("Event created: " + res.toString());

        return "redirect:/events";
    }

    @PostMapping("/send_event")
    public String send_event(Model map) {
        return "list_event_results";
    }
}
