package com.acert.deliverycontrol.application;

import com.acert.deliverycontrol.application.exceptions.DataNotFoundException;
import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.infra.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return this.clientRepository.findAll();
    }

    public Client getClientById(final Long id) {
        return this.clientRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Client not found with ID: " + id));
    }

    @Transactional
    public Client createClient(final Client client) {
        return this.clientRepository.save(client);
    }

    @Transactional
    public Client updateClient(final Long id, final Client updatedClient) {
        return this.clientRepository.findById(id)
                .map(client -> {
                    client.updateClient(updatedClient);
                    return this.clientRepository.save(client);
                })
                .orElseThrow(() -> new DataNotFoundException("Client not found with ID: " + id));
    }

    @Transactional
    public void deleteClient(final Long id) {
        if (this.clientRepository.existsById(id)) {
            this.clientRepository.deleteById(id);
        } else {
            throw new DataNotFoundException("Client not found with ID: " + id);
        }
    }

}

