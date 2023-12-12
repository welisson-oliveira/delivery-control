package com.acert.deliverycontrol.infra.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GrafanaTestController {

    public static void main(final String[] args) {
        try {
            // Executar o login e obter o token
            final String loginUrl = "http://localhost:8081/clients/login";
            final String requestBody = "{\"username\":\"ADMIN@EMAIL.COM\",\"password\":\"admin\"}";

            final HttpURLConnection loginConnection = (HttpURLConnection) new URL(loginUrl).openConnection();
            loginConnection.setRequestMethod("POST");
            loginConnection.setRequestProperty("Content-Type", "application/json");
            loginConnection.setDoOutput(true);

            try (final OutputStream os = loginConnection.getOutputStream()) {
                final byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Verificar se o status da resposta é 200 (OK) antes de tentar obter o token
            if (loginConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final String authHeader = loginConnection.getHeaderField("Authorization");
                // Use o authHeader conforme necessário
                System.out.println("Authorization: " + authHeader);
                GrafanaTestController.makeSubsequentRequests(authHeader);
            } else {
                System.out.println("Falha ao obter o token de autorização. Código de resposta: " +
                        loginConnection.getResponseCode());
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void makeSubsequentRequests(final String authToken) throws Exception {
        // Requisições subsequentes usando o token de autorização
        GrafanaTestController.makeHttpRequest("http://localhost:8081/clients", authToken);
        GrafanaTestController.makeHttpRequest("http://localhost:8081/orders", authToken);
        GrafanaTestController.makeHttpRequest("http://localhost:8081/deliveries", authToken);
    }

    private static void makeHttpRequest(final String url, final String authToken) throws Exception {
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Authorization", "Bearer " + authToken);
        connection.setRequestMethod("GET");

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            final StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            System.out.println("\nResponse from " + url + ":\n" + response.toString());
        }
    }
}
