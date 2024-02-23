package com.kiskee.vocabulary.util;

import com.kiskee.vocabulary.model.dto.token.JweToken;
import com.kiskee.vocabulary.model.entity.user.UserVocabularyApplication;
import com.kiskee.vocabulary.repository.user.projections.UserSecureProjection;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class IdentityUtil {

    private final SimpleGrantedAuthority ROLE_USER = new SimpleGrantedAuthority("ROLE_USER");
    private final SimpleGrantedAuthority OAUTH2_USER = new SimpleGrantedAuthority("OAUTH2_USER");

    public UUID getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthenticated(authentication)) {
            return ((UserSecureProjection) authentication.getPrincipal()).getId();
        }

        return null;
    }

    public Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticated(authentication)) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        return authentication;
    }

    public void setAuthentication(JweToken jweToken) {
        UserVocabularyApplication user = UserVocabularyApplication.builder()
                .setId(jweToken.getId())
                .setUsername(jweToken.getSubject())
                .build();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null,
                jweToken.getAuthorities().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );

        setAuthentication(token);
    }

    public void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isAuthenticated(Authentication authentication) {
        return !Objects.isNull(authentication) && hasUserRole(authentication.getAuthorities());
    }

    private boolean hasUserRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.contains(ROLE_USER) || authorities.contains(OAUTH2_USER);
    }

}
