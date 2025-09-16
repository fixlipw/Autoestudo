package com.ufc.blog.util;

import com.ufc.blog.entity.User;
import com.ufc.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com username: " + username));
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isSelf(Authentication authentication, Long userId) {
        if (authentication == null || userId == null) {
            return false;
        }
        User authenticatedUser = getAuthenticatedUser(authentication);
        return authenticatedUser.getId().equals(userId);
    }

    public void checkOwnershipOrAdmin(Authentication auth, User targetUser) {
        if (auth == null) {
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação necessária.");
        }
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        boolean isOwner = userDetails.getUsername().equals(targetUser.getUsername());
        boolean isAdmin = isAdmin(auth);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado.");
        }
    }
}