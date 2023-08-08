package com.acert.deliverycontrol.infra.dto.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
}
