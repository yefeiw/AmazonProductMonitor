package com.ross.cs504.runninganalysisservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@Document
public class UserInformation {

    @Id
    private String id;

    private String username;
    //in production code, the password must always be encrypted!!
    private String email;

    List<Subscription> subscriptionList;



    public UserInformation(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @JsonCreator
    public UserInformation(@JsonProperty("id")String id,
                 @JsonProperty("username") String username,
                 @JsonProperty("email") String email,
                 @JsonProperty("list") List<Subscription> content) {
        this.id = id;
        this.username = username;
        this.email= email;
        this.subscriptionList = content;
    }

}
