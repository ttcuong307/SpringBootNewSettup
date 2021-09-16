package com.dto.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UpdateUserRequest {
    @JsonProperty(value = "firstName")
    private String firstName;

    @JsonProperty(value = "userName")
    private String userName;

    @JsonProperty(value = "lastName")
    private String lastName;

    @JsonProperty(value = "gender")
    private Integer gender;

    @JsonProperty(value = "provinceId")
    private Integer provinceId;

    @JsonProperty(value = "address")
    private String address;
}
