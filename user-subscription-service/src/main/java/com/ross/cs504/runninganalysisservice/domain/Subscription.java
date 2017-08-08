package com.ross.cs504.runninganalysisservice.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Subscription {
    String category;
    int numbers;
    public Subscription(
            @JsonProperty("category") String category,
            @JsonProperty("numbers") int numbers) {
        this.category = category;
        this.numbers = numbers;
    }
}
