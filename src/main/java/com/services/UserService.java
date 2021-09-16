package com.services;

import com.dto.ErrorDto;
import com.dto.request.LoginRequest;
import com.dto.request.SignUpRequest;
import com.dto.request.UpdateUserRequest;
import com.dto.responses.LoginResponse;
import com.dto.responses.UserInfoResponse;
import com.dto.responses.UserListResponse;
import com.dto.responses.UserResponse;
import com.entities.User;
import com.entities.UserInfo;
import com.enums.Role;
import com.enums.StatusCode;
import com.handling.BadRequestException;
import com.handling.InternationalErrorException;
import com.handling.ResourceNotFoundException;
import com.mappers.UserInfoMapper;
import com.repositories.UserInfoRepository;
import com.repositories.UserRepository;
import com.security.SecurityConstants;
import com.security.UserAuthenticationProvider;
import com.security.UserPrincipal;
import com.utils.MessageSourceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSourceUtil messageSourceUtil;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public ResponseEntity<List<UserListResponse>> getAll() {
        List<User> userList = userRepository.findAll();
        List<UserListResponse> response = userList.stream().map(user -> {
            UserListResponse res = new UserListResponse();
            res.name = user.getUsername();
            return res;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<LoginResponse> signIn(LoginRequest loginRequest) throws ErrorDto {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLoginId(),
                            loginRequest.getPassword()
                    )
            );

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            SecurityContextHolder.getContext().setAuthentication(authentication);

            LoginResponse res = new LoginResponse();
            res.setId(userPrincipal.getId());
            res.setCode(StatusCode.SUCCESS.getCode());
            res.setRole(Role.ROLE_USER);
            res.setToken(userAuthenticationProvider.createToken(userPrincipal));
            res.setTokenType(SecurityConstants.TOKEN_PREFIX.strip());
            res.setExpiredTime(SecurityConstants.EXPIRATION_TIME);
            res.setUserName(userPrincipal.getUsername());
            res.setFirstName(userPrincipal.getFirstname());
            res.setLastName(userPrincipal.getLastname());
            res.setHasInfo(userPrincipal.getHasInfo());
            res.setEmail(userPrincipal.getEmail());
//            userPrincipal
            return ResponseEntity.ok(res);

        } catch (AuthenticationException e) {
            throw new ErrorDto(messageSourceUtil.getMessage("account.error"));
        }
    }

    @Transactional
    public ResponseEntity<UserResponse> signUp(SignUpRequest signUpRequest) throws ErrorDto {

        //TODO: Add validate signUpRequest

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new ErrorDto(messageSourceUtil.getMessage("account.exist"));
        }

        User user = objectMapper.convertValue(signUpRequest, User.class);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Create userInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setGender(signUpRequest.getGender());
        userInfo.setFirstName(signUpRequest.getFirstName());
        userInfo.setLastName(signUpRequest.getLastName());
        userInfo.setAddress(signUpRequest.getAddress());
        userInfo.setProvinceId(signUpRequest.getProvinceId());

        user.setInfo(userInfo);
        user.setHasInfo(true);
        userInfo.setUser(user);
        userRepository.save(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setPassword(user.getPassword());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(userInfo.getFirstName());
        userResponse.setLastName(userInfo.getLastName());
        userResponse.setGender(userInfo.getGender());
        userResponse.setProvinceId(userInfo.getProvinceId());
        userResponse.setAddress(userInfo.getAddress());

        return new ResponseEntity<UserResponse>(userResponse, HttpStatus.CREATED);
    }
    @Transactional
    public ResponseEntity<UserInfoResponse> updateOwnerInfo(long id, UpdateUserRequest updateUserRequest) throws Exception {
        try {
            if (userRepository.existsByIdNotAndUsername(id, updateUserRequest.getUserName())) {
                throw new BadRequestException(messageSourceUtil.getMessage("username.exist"));
            }
            User user = Optional.of(userRepository.getById(id))
                    .orElseThrow();
            Long infoId = null;
            if (updateUserRequest.getUserName() != null && updateUserRequest.getUserName() != ""){
                user.setUsername(updateUserRequest.getUserName());
            }
            user.setHasInfo(user.getInfo() != null);
            if (user.getInfo() == null) {
                // create new info
                UserInfo userInfo = userInfoMapper.toUserInfo(updateUserRequest);
                user.setInfo(userInfo);
                userInfo.setUser(user);
                userRepository.save(user);
                infoId = userInfo.getId();
            } else {
                // update existing info
                UserInfo userInfo = user.getInfo();
                userInfoMapper.updateOwnerInfo(updateUserRequest, userInfo);
                user.setInfo(userInfo);
                userRepository.save(user);
                infoId = userInfo.getId();
            }
            UserInfoResponse res = new UserInfoResponse();
            res.setCode(StatusCode.SUCCESS.getCode());
            res.setMessage(messageSourceUtil.getMessage("account.info.update.success"));
            if (updateUserRequest != null) {
                res.setGender(updateUserRequest.getGender());
                res.setAddress(updateUserRequest.getAddress());
                res.setUserId(user.getId());
                res.setId(infoId);
                res.setProvinceId(updateUserRequest.getProvinceId());
                res.setFirstName(updateUserRequest.getFirstName());
                res.setLastName(updateUserRequest.getLastName());
                res.setHasInfo(user.getHasInfo());
                res.setEmail(user.getEmail());
            }
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (BadRequestException be){

            throw be;
        } catch (EntityNotFoundException ee) {
            throw new ResourceNotFoundException(messageSourceUtil.getMessage("account.notExist"));
        } catch (Exception e){
            e.printStackTrace();
            throw new InternationalErrorException(messageSourceUtil.getMessage("error.server"));
        }
    }

    public ResponseEntity<UserInfoResponse> getOwnerInfo(long id) throws Exception {
        try {
            UserInfoResponse res = new UserInfoResponse();
            User user = Optional.of(userRepository.getById(id))
                    .orElseThrow();
            res.setHasInfo(user.getHasInfo());
            if (user.getInfo() == null) {
                res.setHasInfo(false);
            } else {
                UserInfo userInfo = user.getInfo();
                res = userInfoMapper.toOwnerInfoResponse(userInfo);
                res.setEmail(user.getEmail());
                res.setHasInfo(user.getHasInfo());
            }
            res.setCode(StatusCode.SUCCESS.getCode());

            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (EntityNotFoundException ee) {
            throw new ResourceNotFoundException(messageSourceUtil.getMessage("account.notExist"));
        } catch (Exception e){
            e.printStackTrace();
            throw new InternationalErrorException(messageSourceUtil.getMessage("error.server"));
        }
    }
}
