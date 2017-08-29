package edgecloud.lambda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EventController {

    @GetMapping("/events")
    public String listEvents(Model map) {
        return "events";
    }

    @PostMapping("/send")
    public String send_event(Model map) {
        return "events";
    }
}
