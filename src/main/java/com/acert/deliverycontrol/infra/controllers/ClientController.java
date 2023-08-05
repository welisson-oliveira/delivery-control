package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.application.ClientService;
import com.acert.deliverycontrol.infra.dto.ClientDTO;
import com.acert.deliverycontrol.infra.mappers.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper mapper;

    @GetMapping
    public List<ClientDTO> getAllClients() {
        return this.mapper.toDTOs(this.clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ClientDTO getClientById(@PathVariable final Long id) {
        return this.mapper.toDTO(this.clientService.getClientById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDTO createClient(@RequestBody final ClientDTO client) {
        return this.mapper.toDTO(this.clientService.createClient(this.mapper.toEntity(client)));
    }

    @PutMapping("/{id}")
    public ClientDTO updateClient(@PathVariable final Long id, @RequestBody final ClientDTO client) {
        return this.mapper.toDTO(this.clientService.updateClient(id, this.mapper.toEntity(client)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable final Long id) {
        this.clientService.deleteClient(id);
    }
}

