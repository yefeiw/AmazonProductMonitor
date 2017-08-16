package com.yefeiw.rest;

import com.yefeiw.domain.Subscription;
import com.yefeiw.domain.UserInformation;
import com.yefeiw.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class UserInfoRestController {

    @Autowired
    private UserInfoService service;

    //For Automation:
    // send email to all subscribed users.
    // Using set for Demo.
    //TODO: find a smarter way to do this
    Set<String> users = new HashSet<>();

    @RequestMapping(value = "/users/{username}", method = RequestMethod.GET)
    public List<UserInformation> getUserInfo(@PathVariable String username) {
        return service.findByUsername(username);
    }

    @RequestMapping(value = "/users/{username}", method = RequestMethod.PUT)
    public List<UserInformation> addSubscription(@PathVariable String username, @RequestBody List<Subscription> subscriptions) {
        List<UserInformation> informationList = service.findByUsername(username);
        for (UserInformation cand : informationList) {
            cand.setSubscriptionList(subscriptions);
        }
        return informationList;
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
        for (String user : users) {
            service.findByUsername(user);
        }
    }
}
