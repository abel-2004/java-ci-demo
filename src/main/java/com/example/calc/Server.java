package com.example.calc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Server {

    private static final Calculator CALC = new Calculator();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", Server::handle);
        server.start();
        System.out.println("calc-ci-demo listening on port " + port);
    }

    static void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        try {
            switch (path) {
                case "/health" -> send(exchange, 200, "{\"status\":\"ok\",\"version\":\"" + version() + "\"}");
                case "/add" -> {
                    Map<String, String> q = query(exchange);
                    send(exchange, 200, "{\"result\":" + CALC.add(intParam(q, "a"), intParam(q, "b")) + "}");
                }
                case "/divide" -> {
                    Map<String, String> q = query(exchange);
                    send(exchange, 200, "{\"result\":" + CALC.divide(intParam(q, "a"), intParam(q, "b")) + "}");
                }
                default -> send(exchange, 404, "{\"error\":\"not found\"}");
            }
        } catch (IllegalArgumentException e) {
            send(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    static String version() {
        Package p = Server.class.getPackage();
        String v = p == null ? null : p.getImplementationVersion();
        return v == null ? "dev" : v;
    }

    static Map<String, String> query(HttpExchange exchange) {
        Map<String, String> params = new HashMap<>();
        String raw = exchange.getRequestURI().getRawQuery();
        if (raw != null) {
            for (String pair : raw.split("&")) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    params.put(kv[0], kv[1]);
                }
            }
        }
        return params;
    }

    static int intParam(Map<String, String> params, String name) {
        String value = params.get(name);
        if (value == null) {
            throw new IllegalArgumentException("missing parameter " + name);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be an integer");
        }
    }

    static void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
