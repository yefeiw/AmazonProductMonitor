package com.ross.cs504.runninganalysisservice.rest;

import com.ross.cs504.runninganalysisservice.domain.Subscription;
import com.ross.cs504.runninganalysisservice.domain.UserInformation;
import com.ross.cs504.runninganalysisservice.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserInfoRestController {

    @Autowired
    private UserInfoService service;

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


    @RequestMapping(value = "/runningInfo", method = RequestMethod.POST)
    public List<UserInformation> persistRunningInfo(@RequestBody List<UserInformation> userInformations) {
        return service.saveUserInfo(userInformations);
    }

}
