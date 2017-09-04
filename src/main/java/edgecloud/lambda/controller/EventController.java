
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
import com.alibaba.fastjson.*;

import java.util.*;


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

    @PostMapping("/send_event")
    public String sendEvent(@ModelAttribute Event event) {
        log.info("Sending event: " + event.toString());

        List<EventFunctionMapping> efmaps = efMappingRepository.findByEventName(event.getEventName());
        Event currentEvent = eventRepository.findByEventName(event.getEventName());
        String eventResult = "";
        JSONObject jsonContent = new JSONObject();
        JSONArray funcInfoArray = new JSONArray();

//        jsonContent.put("Event", JSON.parseObject(event.getEventArgs()));
        jsonContent.put("Event", JSON.toJSONString(event.getEventArgs()));
        List<FunctionNodeMap> fnmaps = new ArrayList<>();

        //Get function List.
        for(EventFunctionMapping efmap : efmaps) {
            Integer funcId = efmap.getFuncId();
            List<FunctionNodeMap> tempFnmaps = fnMapRepository.findByFuncId(funcId);
            fnmaps.addAll(tempFnmaps);

            Function currentFunction = functionRepository.findById(funcId);
            String currentFuncName = efmap.getFuncName();
            Integer currentFuncVerion = currentFunction.getFuncVersion();
            JSONObject funcInfo = new JSONObject();
            funcInfo.put("funcName", currentFuncName);
            funcInfo.put("version", currentFuncVerion.toString());
            funcInfoArray.fluentAdd(funcInfo);
        }
        jsonContent.put("funcList", funcInfoArray);

        //Get Node List and send messages.
        List<String> nodeIds = new ArrayList<>();
        for(FunctionNodeMap fnmap : fnmaps) {
            String tempNodeId = fnmap.getNodeId().toString();
            nodeIds.add(tempNodeId);
        }

        String content = JSON.toJSONString(jsonContent);
        for (String nodeId : nodeIds){
            //TODO
            try {
                eventResult = serverAPI.sendMessage(2, nodeId, content);
                currentEvent.setEventResult(eventResult);
                Event res = eventRepository.save(currentEvent);
                log.info("Event and eventResult saved: " + res.toString());
            } catch (Exception e) {
                log.info("Send event failed." + currentEvent.toString());
            }
        }

        return "redirect:/list_event_results";
    }
}
