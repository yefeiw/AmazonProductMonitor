package com.yefeiw.rest;

import com.yefeiw.domain.rabbitconsumer.Consumer;
import com.yefeiw.domain.rabbitconsumer.ConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/consumer")
public class ConsumerRestController {

    @Autowired
    private ConsumerBuilder consumerBuilder;

    private Logger logger = LoggerFactory.getLogger("consumerController");

    private Map<String, Consumer> consumerPool = new HashMap<String, Consumer>();

    @RequestMapping(path = "register/{queueName}", method = RequestMethod.POST)
    String register(
            @PathVariable String queueName,
            @RequestParam(value = "faultyConsumer", required = false, defaultValue = "false") boolean faultyConsumer,
            @RequestParam(value = "runtime", required = false, defaultValue = "0") int runtime
    ) {
        //Register only if not exist
        if(!consumerPool.containsKey(queueName)) {
            consumerPool.put(queueName, consumerBuilder.withRuntime(runtime).build(queueName));
            logger.info("Registered new consumer: "+ queueName);
        } else {
            logger.info(queueName + " already exists, exiting");
        }
        return "Registered new consumer: " + queueName;
    }

}
