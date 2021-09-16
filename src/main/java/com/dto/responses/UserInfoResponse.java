package com.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse extends  Response{

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "ownerId")
    private Long userId;

    @JsonProperty(value = "firstName")
    private String firstName;

    @JsonProperty(value = "lastName")
    private String lastName;

    @JsonProperty(value = "gender")
    private Integer gender;

    @JsonProperty(value = "provinceId")
    private Integer provinceId;

    @JsonProperty(value = "address")
    private String address;

    @JsonProperty(value = "has_info")
    private Boolean hasInfo;

    @JsonProperty(value = "email")
    private String email;
}
