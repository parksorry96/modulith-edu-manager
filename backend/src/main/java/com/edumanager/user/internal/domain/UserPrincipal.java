package com.edumanager.user.internal.domain;

import com.edumanager.shared.domain.enums.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class UserPrincipal  implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;
    private boolean accountNonLocked;


    //TODO : AUTHORITIY CACHE 공부하기
    private static final Map<UserRole, SimpleGrantedAuthority> AUTHORITIES_CACHE=
            Arrays.stream(UserRole.values())
                    .collect(Collectors.toMap(
                            Function.identity(),
                            role -> new SimpleGrantedAuthority(role.getAuthority()),
                            (existing, replacement) -> existing,
                            ()->new EnumMap<>(UserRole.class)
                    ));


    public static UserPrincipal create(User user){
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(AUTHORITIES_CACHE::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .enabled(user.isActive())
                .accountNonLocked(!user.isAccountLocked())
                .build();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
