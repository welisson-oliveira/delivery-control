package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.application.DeliveryService;
import com.acert.deliverycontrol.infra.dto.delivery.DeliveryDTO;
import com.acert.deliverycontrol.infra.mappers.DeliveryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.acert.deliverycontrol.infra.config.redis.OpenApiConfig.SECURITY_CONFIG_NAME;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryMapper mapper;

    @Operation(summary = "Obtém todas as entregas", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de entregas obtida com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeliveryDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<DeliveryDTO> getAllDeliveries() {
        return this.mapper.toDTOs(this.deliveryService.getAllDeliveries());
    }

    @Operation(summary = "Obtém uma entrega pelo ID", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrega obtida com sucesso",
                    content = @Content(schema = @Schema(implementation = DeliveryDTO.class))),
            @ApiResponse(responseCode = "400", description = "ID da entrega inválido"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "404", description = "Entrega não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public DeliveryDTO getDeliveryById(
            @Parameter(description = "ID da entrega a ser obtida")
            @PathVariable final Long id) {
        return this.mapper.toDTO(this.deliveryService.getDeliveryById(id));
    }

    // TODO - pesquisas personalizadas

}

