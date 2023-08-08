# delivery-control

Sistema de controle de entrega desenvolvido para processo seletivo no grupo Acert

# SUBIR LOCALMENTE

Necessario docker instalado

1. Rodar o docker compose que se encontra em: *src/main/resources*
   ```bash
   docker-compose up -d

2. Subir o projeto
   ```bash
   mvn spring-boot:run

## A aplicação possui dois usuarios

* ADMIN - POSSUI AS CREDENCIAIS DE ADMIN E CLIENT
    - user: ADMIN@EMAIL.COM
    - password: admin

* CLIENT - POSSUI APENAS A CREDENCIAL CLIENT
    - user: CLIENT@EMAIL.COM
    - password: client