package com.controllers;

import com.dto.ErrorDto;
import com.dto.request.LoginRequest;
import com.dto.request.SignUpRequest;
import com.dto.request.UpdateUserRequest;
import com.dto.responses.UserInfoResponse;
import com.dto.responses.UserResponse;
import com.dto.responses.LoginResponse;
import com.dto.responses.UserListResponse;
import com.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/owners")
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserListResponse>> getAll() {
        return userService.getAll();
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@Valid @RequestBody LoginRequest loginRequest) throws ErrorDto {
        return userService.signIn(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) throws ErrorDto {
        return userService.signUp(signUpRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserInfoResponse> updateOwnerInfo(@PathVariable("id") long id, @Validated @RequestBody UpdateUserRequest updateUserRequest) throws Exception{
        return userService.updateOwnerInfo(id, updateUserRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoResponse> getOwnerInfo(@PathVariable("id") long id) throws Exception{
        return userService.getOwnerInfo(id);
    }

}
