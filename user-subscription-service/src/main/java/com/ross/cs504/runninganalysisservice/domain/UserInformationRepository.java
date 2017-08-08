package com.ross.cs504.runninganalysisservice.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInformationRepository extends MongoRepository<UserInformation, String> {
    List<UserInformation> findByUsername(@Param("username") String username);
    List<UserInformation> findByEmail(@Param("email") String email);
}
