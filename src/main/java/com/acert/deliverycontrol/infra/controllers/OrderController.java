package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.application.OrderService;
import com.acert.deliverycontrol.domain.order.OrderStatus;
import com.acert.deliverycontrol.infra.dto.order.CreateUpdateOrderDTO;
import com.acert.deliverycontrol.infra.dto.order.OrderDTO;
import com.acert.deliverycontrol.infra.mappers.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static com.acert.deliverycontrol.infra.config.redis.OpenApiConfig.SECURITY_CONFIG_NAME;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper mapper;

    @Operation(summary = "Obtém todas os pedidos", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtida com sucesso",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso ao recurso é proibido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<OrderDTO> getAllOrders() {
        return this.mapper.toDTOs(this.orderService.getAllOrders());
    }

    @Operation(summary = "Obtém uma pedido por ID", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido obtido com sucesso",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso ao recurso é proibido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public OrderDTO getOrderById(@Parameter(description = "ID do pedido a ser pesquisado") @PathVariable final Long id) {
        return this.mapper.toDTO(this.orderService.getOrderById(id));
    }

    @Operation(summary = "Obtém todos os pedidos ativados", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtidos com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))}),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso ao recurso é proibido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/activated")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<OrderDTO> getAllActivatedOrder() {
        return this.mapper.toDTOs(this.orderService.getAllActivatedOrders());
    }

    @Operation(summary = "Obtém todos os pedidos finalizados", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtidos com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))}),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso ao recurso é proibido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/finished")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<OrderDTO> getAllFinishedOrder() {
        return this.mapper.toDTOs(this.orderService.getAllFinishedOrders());
    }

    @Operation(summary = "Obtém todos os pedidos cancelados", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtidos com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))}),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso ao recurso é proibido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/canceled")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Set<OrderDTO> getAllCanceledOrder() {
        return this.mapper.toDTOs(this.orderService.getAllCanceledOrders());
    }

    @Operation(summary = "Obtém todos os pedidos ativos de um cliente específico", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtidos com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))}),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/clients/{clientId}/activated")
    @PreAuthorize("@security.sameUserOrAdmin(#clientId)")
    public Set<OrderDTO> getActivatedOrdersByClientId(@Parameter(description = "ID do cliente para buscar os pedidos ativos")
                                                      @PathVariable final Long clientId) {
        return this.mapper.toDTOs(this.orderService.getActivatedOrdersByClientId(clientId));
    }

    @Operation(summary = "Obtém todos os pedidos concluídos de um cliente específico", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtidos com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))}),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/clients/{clientId}/finished")
    @PreAuthorize("@security.sameUserOrAdmin(#clientId)")
    public Set<OrderDTO> getDoneOrdersByClientId(@Parameter(description = "ID do cliente para buscar os pedidos concluídos")
                                                 @PathVariable final Long clientId) {
        return this.mapper.toDTOs(this.orderService.getDoneOrdersByClientId(clientId));
    }

    @Operation(summary = "Obtém todos os pedidos cancelados de um cliente específico", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtidos com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))}),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/clients/{clientId}/canceled")
    @PreAuthorize("@security.sameUserOrAdmin(#clientId)")
    public Set<OrderDTO> getCanceledOrdersByClientId(
            @Parameter(description = "ID do cliente para buscar os pedidos cancelados")
            @PathVariable final Long clientId) {
        return this.mapper.toDTOs(this.orderService.getCanceledOrdersByClientId(clientId));
    }

    @Operation(summary = "Cria um novo pedido", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Pedido inválido"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CLIENT')")
    public OrderDTO createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Pedido a ser criado", required = true)
            @RequestBody final CreateUpdateOrderDTO order) {
        OrderController.log.info("Criando a pedido: '" + order.getDescription() + "'");
        return this.mapper.toDTO(this.orderService.createOrder(order.getDescription()));
    }

    @Operation(summary = "Atualiza um pedido existente", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Pedido inválido"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public OrderDTO updateOrder(
            @Parameter(description = "ID do pedido a ser atualizado")
            @PathVariable final Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes do pedido a ser atualizado", required = true)
            @RequestBody final CreateUpdateOrderDTO order) {
        return this.mapper.toDTO(this.orderService.updateOrder(id, order.getDescription()));
    }

    @Operation(summary = "Atualiza o status de um pedido para 'Em progresso'", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso para 'Em progresso'",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Pedido inválido"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PatchMapping("/{id}/in-progress")
    @PreAuthorize("hasAuthority('ADMIN')")
    public OrderDTO start(
            @Parameter(description = "ID do pedido cujo status deve ser atualizado para 'Em progresso'")
            @PathVariable final Long id) {
        return this.mapper.toDTO(this.orderService.changeStatus(id, OrderStatus.IN_PROGRESS));
    }

    @Operation(summary = "Atualiza o status de um pedido para 'Cancelado'", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso para 'Cancelado'",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Pedido inválido"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PatchMapping("/{id}/canceled")
    @PreAuthorize("hasAuthority('ADMIN')")
    public OrderDTO cancel(
            @Parameter(description = "ID do pedido cujo status deve ser atualizado para 'Cancelado'")
            @PathVariable final Long id) {
        return this.mapper.toDTO(this.orderService.changeStatus(id, OrderStatus.CANCELED));
    }

    @Operation(summary = "Atualiza o status de um pedido para 'Concluído'", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso para 'Concluído'",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Pedido inválido"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PatchMapping("/{id}/finished")
    @PreAuthorize("hasAuthority('ADMIN')")
    public OrderDTO finished(
            @Parameter(description = "ID do pedido cujo status deve ser atualizado para 'Concluído'")
            @PathVariable final Long id) {
        return this.mapper.toDTO(this.orderService.changeStatus(id, OrderStatus.DONE));
    }


    @Operation(summary = "Deleta um pedido específico", security = @SecurityRequirement(name = SECURITY_CONFIG_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Pedido inválido"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Proibido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteOrder(
            @Parameter(description = "ID do pedido a ser deletado")
            @PathVariable final Long id) {
        this.orderService.deleteOrder(id);
    }

}

