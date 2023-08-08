package com.acert.deliverycontrol.config.mockauth;

import com.acert.deliverycontrol.domain.client.Client;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ClientSecurityContext extends DeliveryControlContextFactory<WithUser> {

    @Override
    public SecurityContext createSecurityContext(final WithUser annotation) {
        final Client client = new Client(annotation.id(), annotation.name(), annotation.username(), annotation.phoneNumber(), annotation.address());

        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        final PreAuthenticatedAuthenticationToken token = new ClientAuthenticatedToken(client, null, Arrays.stream(annotation.authorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        securityContext.setAuthentication(token);
        return securityContext;
    }
}
