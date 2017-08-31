package edgecloud.lambda.controller;

import edgecloud.lambda.entity.Event;
import edgecloud.lambda.entity.EventFunctionMapping;
import edgecloud.lambda.entity.Function;
import edgecloud.lambda.entity.FunctionNodeMap;
import edgecloud.lambda.entity.Node;
import edgecloud.lambda.repository.EventRepository;
import edgecloud.lambda.repository.FunctionRepository;
import edgecloud.lambda.repository.NodeRepository;
import edgecloud.lambda.utils.StubServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private EventRepository eventRepository;

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

        List<Function> functions = functionRepository.findAll();
        List<Node> nodes = StubServer.queryNodes();
        FunctionNodeMap fnm = new FunctionNodeMap();  // for save user choice

        map.addAttribute("functions", functions);
        map.addAttribute("nodes", nodes);
        map.addAttribute("fnm", fnm);

        return "push_function";
    }

    @GetMapping("/map")
    public String mapFuncWithEvent(Model map) {
        return "mapping";
    }

    @GetMapping("/send_event")
    public String sendEvent(Model map) {
        List<Event> events = eventRepository.findAll();
        map.addAttribute("events", events);
        map.addAttribute("event", new Event());
        return "send_event";
    }

    @GetMapping("/create_event_func_map")
    public String createEventFunctionMapping(Model map) {
        List<Function> functions = functionRepository.findAll();
        map.addAttribute("functions", functions);
        map.addAttribute("mapping", new EventFunctionMapping());
        return "create_mapping";
    }

}
