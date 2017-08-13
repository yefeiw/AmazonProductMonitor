package com.yefeiw.service;

import com.yefeiw.domain.UserInformation;

import java.util.List;

public interface UserInfoService {
    List<UserInformation> saveUserInfo(List<UserInformation> userInfo);
    List<UserInformation> findByUsername(String username);
    List<UserInformation> findByEmail (String email);
}
