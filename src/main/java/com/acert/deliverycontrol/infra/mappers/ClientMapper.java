package com.acert.deliverycontrol.infra.mappers;

import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.infra.dto.client.ClientDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClientMapper {

    private final PasswordEncoder encoder;

    public ClientDTO toDTO(final Client client) {
        final ClientDTO clientDTO = new ClientDTO(client.getId(), client.getName(), client.getEmail(), client.getPhoneNumber(), client.getAddress(), null);
        clientDTO.addAuthorities(client.getRoles());
        return clientDTO;
    }

    public Client toEntity(final ClientDTO dto) {
        final String password = Objects.isNull(dto.getPassword()) ? null : this.encoder.encode(dto.getPassword());
        return new Client(dto.getId(), dto.getName(), dto.getEmail(), dto.getPhoneNumber(), dto.getAddress(), password);
    }

    public List<ClientDTO> toDTOs(final List<Client> allClients) {
        return allClients.stream().map(this::toDTO).collect(Collectors.toList());
    }
}

