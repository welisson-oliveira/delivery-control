package com.acert.deliverycontrol.infra.config;

import com.acert.deliverycontrol.domain.client.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("security")
public class MySecurityService {

    public boolean sameUserOrAdmin(final Long userId) {

        final Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (authorities.stream().anyMatch(authority -> "ADMIN".equals(authority.getAuthority()))) {
            return true;
        }
        final Client client = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return client.getId().equals(userId);
    }

}
