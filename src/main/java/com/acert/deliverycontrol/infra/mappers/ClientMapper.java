package com.acert.deliverycontrol.infra.mappers;

import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.infra.dto.ClientDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientMapper {

    public ClientDTO toDTO(final Client client) {
        return new ClientDTO(client.getId(), client.getName(), client.getEmail(), client.getPhoneNumber(), client.getAddress());
    }

    public Client toEntity(final ClientDTO dto) {
        return new Client(dto.getId(), dto.getName(), dto.getEmail(), dto.getPhoneNumber(), dto.getAddress());
    }

    public List<ClientDTO> toDTOs(final List<Client> allClients) {
        return allClients.stream().map(this::toDTO).collect(Collectors.toList());
    }
}

