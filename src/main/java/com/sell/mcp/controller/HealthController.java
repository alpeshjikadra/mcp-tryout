package com.sell.mcp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "MCP Server",
                "version", "1.0.0"
        );
    }

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
                "message", "MCP Server is running",
                "mcp_endpoint", "/mcp",
                "health_endpoint", "/health"
        );
    }
}
