package com.ross.cs504.runninganalysisservice.service;

import com.ross.cs504.runninganalysisservice.domain.UserInformation;

import java.util.List;

public interface UserInfoService {
    List<UserInformation> saveUserInfo(List<UserInformation> userInfo);
    List<UserInformation> findByUsername(String username);
    List<UserInformation> findByEmail (String email);
}
