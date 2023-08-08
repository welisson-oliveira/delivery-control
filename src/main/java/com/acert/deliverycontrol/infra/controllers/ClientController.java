package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.application.ClientService;
import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.infra.config.security.JwtTokenUtil;
import com.acert.deliverycontrol.infra.dto.AuthRequest;
import com.acert.deliverycontrol.infra.dto.ClientDTO;
import com.acert.deliverycontrol.infra.mappers.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ClientDTO> getAllClients() {
        return this.mapper.toDTOs(this.clientService.getAllClients());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ClientDTO getClientById(@PathVariable final Long id) {
        return this.mapper.toDTO(this.clientService.getClientById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ClientDTO createClient(@RequestBody final ClientDTO client) {
        return this.mapper.toDTO(this.clientService.createClient(this.mapper.toEntity(client)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ClientDTO updateClient(@PathVariable final Long id, @RequestBody final ClientDTO client) {
        return this.mapper.toDTO(this.clientService.updateClient(id, this.mapper.toEntity(client)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteClient(@PathVariable final Long id) {
        this.clientService.deleteClient(id);
    }

    @PostMapping("/login")
    public ResponseEntity<ClientDTO> login(@RequestBody final AuthRequest request) {
        try {
            final Authentication authenticate = this.authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                    );

            final Client client = (Client) authenticate.getPrincipal();

            final String token = this.jwtTokenUtil.generateToken(client);
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(this.mapper.toDTO(client));
        } catch (final BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}

