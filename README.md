# delivery-control

Sistema de controle de entrega desenvolvido para estudos

# SUBIR LOCALMENTE

Necessario docker instalado

1. Rodar o docker compose que se encontra em: *src/main/resources*
   ```bash
   docker-compose up -d

2. Subir o projeto
   ```bash
   mvn spring-boot:run

## Swagger

http://localhost:8080/swagger-ui/index.html#/

## A aplicação possui dois usuarios

* ADMIN - POSSUI AS CREDENCIAIS DE ADMIN E CLIENT
    - user: ADMIN@EMAIL.COM
    - password: admin
    - {
      "username": "ADMIN@EMAIL.COM",
      "password": "admin"
      }

* CLIENT - POSSUI APENAS A CREDENCIAL CLIENT
    - user: CLIENT@EMAIL.COM
    - password: client

### Fluxo principal

* Criar Pedido
    - Cria quantos pedidos quiser (o primeiro cria uma delivery e os subsequentes são adicionados a esse delivery)
    - Muda status para em progresso
    - Muda status para concluido (quando todas os pedidos de um mesmo delivery forem concluidos o delivery muda para em
      progresso)
    - Finaliza delivery

### Demais operações

* Pesquisas
    - Clientes podem pesquisar seus pedidos concluidos, cancelados e ativos
    - Adms podem pesquisar todos os pedidos concluidos, cancelados e ativos ou por cliente
    - Adms podem lisar todas as deliverys ou por id
* CRUD basico de clients
* Se todos os pedidos de uma entrega forem cancelado, a entrega tbm é cancelada
* Se todos os pedidos de uma entrega forem excluidos, a entrega tbm é excluida

---

* Melhorias **(não implementadas por falta de tempo)**
    - validações
    - gerenciamento de roles
    - retornar roles no client
    - paginação nos getters
    - alteração de senha
    - configurar liquibase para executar diff/updade/etc
    - criar os rollbacks do liquibase
    - criar tabela para endereço
    - criar um controller advice para personalizar as exceptions