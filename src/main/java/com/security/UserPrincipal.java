package com.security;

import com.entities.User;
import com.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private Boolean hasInfo;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public static UserPrincipal create(User user) {

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_OWNER.name()));

        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId(user.getId());
        userPrincipal.setUsername(user.getUsername());
        userPrincipal.setEmail(user.getEmail());
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setHasInfo(user.getHasInfo());
        if (user.getInfo() != null){
            userPrincipal.setFirstname(user.getInfo().getFirstName());
            userPrincipal.setLastname(user.getInfo().getLastName());
        } else {
            userPrincipal.setHasInfo(false);
        }

        userPrincipal.setAuthorities(authorities);

        return userPrincipal;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return null;
    }
}
