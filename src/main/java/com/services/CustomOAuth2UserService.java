package com.services;

import com.entities.User;
import com.entities.UserInfo;
import com.enums.AuthProvider;
import com.handling.OAuth2AuthenticationProcessingException;
import com.repositories.UserRepository;
import com.security.OAuth2.user.OAuth2UserInfo;
import com.security.OAuth2.user.OAuth2UserInfoFactory;
import com.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes());
        if ((oAuth2UserInfo.getEmail()).isEmpty()) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 Provider");
        }
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(oAuth2UserInfo.getName(), oAuth2UserInfo.getEmail());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("You're signed up with "
                        + user.getProvider() + " account. Please use your " + oAuth2UserRequest.getClientRegistration().getRegistrationId()
                        + " account to Login");
            }
            user = updateExistingUser(user);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return UserPrincipal.create(user);
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setUsername(oAuth2UserInfo.getEmail());
        user.setEmailVerified((Boolean) oAuth2UserInfo.getAttributes().get("email_verified"));
        user.setHasInfo(false);
        UserInfo info = new UserInfo();
        info.setUser(user);
        info.setLastName(oAuth2UserInfo.getAttributes().get("name").toString());
        user.setInfo(info);
        return userRepository.save(user);
    }

    private User updateExistingUser(User user) {
        return userRepository.save(user);
    }

}
