package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.application.ClientService;
import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.infra.config.security.JwtTokenUtil;
import com.acert.deliverycontrol.infra.dto.AuthRequest;
import com.acert.deliverycontrol.infra.dto.client.ClientDTO;
import com.acert.deliverycontrol.infra.mappers.ClientMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import static com.acert.deliverycontrol.infra.config.redis.OpenApiConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "Obter todos os clientes", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes obtida com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ClientDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Acesso negado"),
            @ApiResponse(responseCode = "403", description = "Acesso ao recurso é proibido"),
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro interno")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ClientDTO> getAllClients() {
        return this.mapper.toDTOs(this.clientService.getAllClients());
    }

    @Operation(summary = "Obter detalhes de um cliente específico", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes do cliente obtidos com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso ao recurso é proibido"),
            @ApiResponse(responseCode = "404", description = "O recurso que você estava tentando alcançar não foi encontrado"),
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro interno")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ClientDTO getClientById(@Parameter(description = "ID do cliente a ser pesquisado") @PathVariable final Long id) {
        return this.mapper.toDTO(this.clientService.getClientById(id));
    }

    @Operation(summary = "Criar um novo cliente", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso ao recurso é proibido"),
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro interno")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ClientDTO createClient(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Cliente a ser criado", required = true) @RequestBody final ClientDTO client) {
        return this.mapper.toDTO(this.clientService.createClient(this.mapper.toEntity(client)));
    }

    @Operation(summary = "Atualizar um cliente existente", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso ao recurso é proibido"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro interno")
    })
    @PutMapping("/{id}")
    @PreAuthorize("@security.sameUserOrAdmin(#clientId)")
    public ClientDTO updateClient(@Parameter(description = "id do cliente a ser atualizado") @PathVariable final Long id,
                                  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do cliente a serem atualizados", required = true) @RequestBody final ClientDTO client) {
        return this.mapper.toDTO(this.clientService.updateClient(id, this.mapper.toEntity(client)));
    }

    @Operation(summary = "Excluir um cliente existente", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso ao recurso é proibido"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro interno")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteClient(@Parameter(description = "id do cliente a ser excluído") @PathVariable final Long id) {
        this.clientService.deleteClient(id);
    }

    @Operation(summary = "Autenticar um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente autenticado com sucesso",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Ocorreu um erro interno")
    })
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

