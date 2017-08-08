package com.ross.cs504.runninganalysisservice.service.impl;

import com.ross.cs504.runninganalysisservice.domain.UserInformation;
import com.ross.cs504.runninganalysisservice.domain.UserInformationRepository;
import com.ross.cs504.runninganalysisservice.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInformationRepository repository;

    @Override
    public List<UserInformation> saveUserInfo(List<UserInformation> userInfo) {
        return repository.save(userInfo);

    }
    @Override
    public List<UserInformation> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public List<UserInformation> findByEmail(String email) {
        return repository.findByEmail(email);
    }


}
