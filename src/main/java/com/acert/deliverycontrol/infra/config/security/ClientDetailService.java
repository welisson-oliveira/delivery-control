package com.acert.deliverycontrol.infra.config.security;

import com.acert.deliverycontrol.application.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientDetailService implements UserDetailsService {

    private final ClientService clientService;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final UserDetails client = this.clientService.getByEmail(email);
        if (client != null) {
            return client;
        } else {
            throw new UsernameNotFoundException("Client not found");
        }
    }
}
