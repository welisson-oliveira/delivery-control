package com.acert.deliverycontrol.application;

import com.acert.deliverycontrol.application.exceptions.DataNotFoundException;
import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.domain.client.Role;
import com.acert.deliverycontrol.infra.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private static final String CLIENT_NOT_FOUND_WITH_ID = "Client not found with ID:";
    private final ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return this.clientRepository.findAll();
    }

    public Client getClientById(final Long id) {
        return this.clientRepository.findById(id).orElseThrow(() -> new DataNotFoundException(ClientService.CLIENT_NOT_FOUND_WITH_ID + " " + id));
    }

    @Transactional
    public Client createClient(final Client client) {
        return this.clientRepository.save(client);
    }

    @Transactional
    public Client updateClient(final Long id, final Client updatedClient) {
        final Client loggedClient = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return this.clientRepository.findById(id)
                .map(client -> {
                    if (client.getId().equals(loggedClient.getId())) {
                        client.updateClient(updatedClient);
                    }
                    return this.clientRepository.save(client);
                })
                .orElseThrow(() -> new DataNotFoundException(ClientService.CLIENT_NOT_FOUND_WITH_ID + " " + id));
    }

    @Transactional
    public void deleteClient(final Long id) {
        if (this.clientRepository.existsById(id)) {
            this.clientRepository.deleteById(id);
        } else {
            throw new DataNotFoundException(ClientService.CLIENT_NOT_FOUND_WITH_ID + " " + id);
        }
    }

    public UserDetails getByEmail(final String email) {
        return this.clientRepository.findByEmail(email).orElseThrow(() -> new DataNotFoundException("Client not found with Email: " + email));
    }

    public List<Role> getRolesByClientId(final Long id) {
        return this.getClientById(id).getRoles();
    }
}

