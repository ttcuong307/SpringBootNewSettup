package com.mappers;

import com.dto.request.UpdateUserRequest;
import com.dto.responses.UserInfoResponse;
import com.entities.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserInfoMapper {
    UserInfoMapper INSTANCE = Mappers.getMapper(UserInfoMapper.class);

    UserInfo toUserInfo(UpdateUserRequest updateUserRequest);

    void updateOwnerInfo(UpdateUserRequest updateUserRequest, @MappingTarget UserInfo userInfo);
    @Mapping(target="userId", source="info.user.id")
    UserInfoResponse toOwnerInfoResponse(UserInfo info);
}
