package com.acert.deliverycontrol.config.mockauth;

import com.acert.deliverycontrol.domain.client.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Collection;
import java.util.List;

public class ClientAuthenticatedToken extends PreAuthenticatedAuthenticationToken {

    public ClientAuthenticatedToken(final Client client, final List<String> roles, final Collection<? extends GrantedAuthority> authorities) {
        super(client, roles, authorities);
    }
}
