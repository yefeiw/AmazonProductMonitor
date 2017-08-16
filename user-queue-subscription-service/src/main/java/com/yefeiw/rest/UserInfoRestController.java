package com.yefeiw.rest;

import com.yefeiw.domain.AdRepository;
import com.yefeiw.domain.Subscription;
import com.yefeiw.domain.UserInformation;
import com.yefeiw.domain.rabbitconsumer.Consumer;
import com.yefeiw.domain.rabbitconsumer.ConsumerBuilder;
import com.yefeiw.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserInfoRestController {

    @Autowired
    private UserInfoService service;
    @Autowired
    private ConsumerBuilder builder;
    @Autowired
    private AdRepository repository;


    private Map<String, Consumer> consumerPool = new HashMap<String, Consumer>();
    private Logger logger = LoggerFactory.getLogger(UserInfoRestController.class);
    //For Automation:
    // send email to all subscribed users.
    // Using set for Demo.
    //TODO: find a smarter way to do this
    Set<String> users = new HashSet<>();

    @RequestMapping(value = "/users/{username}", method = RequestMethod.GET)
    public List<UserInformation> getUserInfo(@PathVariable String username) {
        String queueName = "recommendation";
        if(!consumerPool.containsKey(queueName)) {
            consumerPool.put(queueName, builder.build(queueName));
            logger.info("Registered new consumer: "+ queueName);
        } else {
            logger.info(queueName + " already exists, exiting");
        }
        return service.findByUsername(username);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public List<UserInformation> persistRunningInfo(@RequestBody List<UserInformation> userInformations) {
        for(UserInformation information : userInformations) {
            users.add(information.getUsername());
        }
        return service.saveUserInfo(userInformations);
    }

    //Send email to all registered users
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public void update() {
        String queueName = "recommendation";
        if(!consumerPool.containsKey(queueName)) {
            consumerPool.put(queueName, builder.build(queueName));
            logger.info("Registered new consumer: "+ queueName);
        } else {
            logger.info(queueName + " already exists, exiting");
        }
        for (String user : users) {
            service.findByUsername(user);
        }
    }
}
