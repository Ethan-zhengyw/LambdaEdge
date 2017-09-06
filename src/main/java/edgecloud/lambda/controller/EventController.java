
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

        //Get function List.
        for(EventFunctionMapping efmap : efmaps) {
            log.info("Event function mapping = " + efmap.toString());

            //Construct message content.
            JSONObject jsonContent = new JSONObject();
            JSONArray funcInfoArray = new JSONArray();
            List<FunctionNodeMap> fnmaps = new ArrayList<>();
            JSONObject funcInfo = new JSONObject();

            jsonContent.put("Event", JSON.toJSONString(JSON.parseObject(event.getEventArgs())));
            Integer funcId = efmap.getFuncId();
            Function currentFunction = functionRepository.findById(funcId);
            String currentFuncName = efmap.getFuncName();
            Integer currentFuncVerion = currentFunction.getFuncVersion();
            funcInfo.put("funcName", currentFuncName);
            funcInfo.put("version", currentFuncVerion.toString());
            funcInfoArray.fluentAdd(funcInfo);
            jsonContent.put("funcList", funcInfoArray);
            String content = JSON.toJSONString(jsonContent);

            //Get function node maps.
            List<FunctionNodeMap> tempFnmaps = fnMapRepository.findByFuncId(funcId);
            fnmaps.addAll(tempFnmaps);

            for(FunctionNodeMap fnmap : fnmaps) {
                log.info("Function node mapping = " + fnmap.toString());
                Integer nodeId = fnmap.getNodeId();
                String result = "";
                EventResult currentEventResult = new EventResult();

                try {
                    //Send event and set event result.
                    result = serverAPI.sendMessage(2, nodeId.toString(), content);
                    log.info("Function result = " + result);

                    currentEventResult.setEventId(event.getId());
                    currentEventResult.setEventName(event.getEventName());
                    currentEventResult.setFuncId(funcId);
                    currentEventResult.setNodeId(nodeId);
                    currentEventResult.setEventResult(result);

                    //Set the finish time.
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String timeStamp = dateFormat.format(new Date());
                    currentEventResult.setFinishTime(timeStamp);

                    //Save event result.
                    EventResult eventResult = eventResultRepository.save(currentEventResult);
                    log.info("Event and eventResult saved: " + eventResult.toString());
                } catch (Exception e) {
                    log.info("Send event failed." + efmap.toString());
                }
            }
        }

        return "redirect:/list_event_results";
    }
}
