
package edgecloud.lambda.controller;

import edgecloud.deviceserver.ServerAPI;
import edgecloud.lambda.entity.*;

import edgecloud.lambda.repository.EventRepository;
import edgecloud.lambda.repository.EventResultRepository;
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

import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class EventController {
    ServerAPI serverAPI = new ServerAPI();

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventResultRepository eventResultRepository;

    @Autowired
    private EventFunctionMappingRepository efMappingRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private FunctionNodeMapRepository fnMapRepository;

    @GetMapping("/list_event_results")
    public String listEventResults(Model map) {
        log.info("Querying event results...");
        List<EventResult> eventResults = eventResultRepository.findAll();

        for (EventResult eventResult : eventResults) {
            log.info(eventResult.toString());
        }

        map.addAttribute("eventResults", eventResults);
        return "pages/list_event_results";
    }

    @PostMapping("/send_event")
    public String sendEvent(@ModelAttribute Event event) {
        log.info("Sending event: " + event.toString());

        List<EventFunctionMapping> efmaps = efMappingRepository.findByEventName(event.getEventName());
//        Event currentEvent = eventRepository.findByEventName(event.getEventName());
        EventResult currentEventResult = new EventResult();
        String result = "";
        JSONObject jsonContent = new JSONObject();
        JSONArray funcInfoArray = new JSONArray();

//        jsonContent.put("Event", JSON.parseObject(event.getEventArgs()));
        jsonContent.put("Event", JSON.toJSONString(JSON.parseObject(event.getEventArgs())));
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
        List<Integer> nodeIds = new ArrayList<>();
        for(FunctionNodeMap fnmap : fnmaps) {
            Integer tempNodeId = fnmap.getNodeId();
            nodeIds.add(tempNodeId);
        }

        String content = JSON.toJSONString(jsonContent);
        for (Integer nodeId : nodeIds){
            //TODO
            try {
                result = serverAPI.sendMessage(2, nodeId.toString(), content);
                currentEventResult.setEventId(event.getId());
                currentEventResult.setEventName(event.getEventName());
//                currentEventResult.setFuncId();
                currentEventResult.setNodeId(nodeId);
                currentEventResult.setEventResult(result);

                //Set the finish time.
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeStamp = dateFormat.format(new Date());
                currentEventResult.setFinishTime(timeStamp);

                EventResult eventResult = eventResultRepository.save(currentEventResult);
                log.info("Event and eventResult saved: " + eventResult.toString());
            } catch (Exception e) {
                log.info("Send event failed." + currentEventResult.toString());
            }
        }

        return "redirect:/list_event_results";
    }
}
