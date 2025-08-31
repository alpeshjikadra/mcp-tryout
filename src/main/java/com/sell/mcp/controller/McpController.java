package com.sell.mcp.controller;

import com.sell.mcp.protocol.McpMessage;
import com.sell.mcp.tool.LeadCreationTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/mcp")
public class McpController {
    
    private static final Logger logger = LoggerFactory.getLogger(McpController.class);
    
    private final LeadCreationTool leadCreationTool;
    private final Map<String, Sinks.Many<String>> clientSinks = new ConcurrentHashMap<>();
    
    public McpController(LeadCreationTool leadCreationTool) {
        this.leadCreationTool = leadCreationTool;
    }
    
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> connect(@RequestParam(defaultValue = "default") String clientId) {
        logger.info("MCP client connected: {}", clientId);
        
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        clientSinks.put(clientId, sink);
        
        // Send initial connection message
        sendMessage(clientId, new McpMessage(null, Map.of(
            "protocolVersion", "2024-11-05",
            "capabilities", Map.of(
                "tools", Map.of("listChanged", true)
            ),
            "serverInfo", Map.of(
                "name", "Lead Creation MCP Server",
                "version", "1.0.0"
            )
        )));
        
        return sink.asFlux()
            .doOnCancel(() -> {
                logger.info("MCP client disconnected: {}", clientId);
                clientSinks.remove(clientId);
            })
            .doOnError(error -> {
                logger.error("Error in MCP connection for client {}: {}", clientId, error.getMessage());
                clientSinks.remove(clientId);
            });
    }
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public McpMessage handleMessage(@RequestBody McpMessage message, 
                                   @RequestParam(defaultValue = "default") String clientId) {
        logger.info("Received MCP message: method={}, id={}", message.getMethod(), message.getId());
        
        try {
            return switch (message.getMethod()) {
                case "initialize" -> handleInitialize(message);
                case "tools/list" -> handleToolsList(message);
                case "tools/call" -> handleToolCall(message);
                default -> new McpMessage(message.getId(), 
                    new McpMessage.McpError(-32601, "Method not found: " + message.getMethod()));
            };
        } catch (Exception e) {
            logger.error("Error handling MCP message: {}", e.getMessage(), e);
            return new McpMessage(message.getId(), 
                new McpMessage.McpError(-32603, "Internal error: " + e.getMessage()));
        }
    }
    
    private McpMessage handleInitialize(McpMessage message) {
        logger.info("Handling initialize request");
        return new McpMessage(message.getId(), Map.of(
            "protocolVersion", "2024-11-05",
            "capabilities", Map.of(
                "tools", Map.of("listChanged", true)
            ),
            "serverInfo", Map.of(
                "name", "Lead Creation MCP Server",
                "version", "1.0.0"
            )
        ));
    }
    
    private McpMessage handleToolsList(McpMessage message) {
        logger.info("Handling tools/list request");
        
        List<Map<String, Object>> tools = List.of(
            Map.of(
                "name", "create_lead",
                "description", "Create a new lead in Kylas CRM with first name and last name",
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "firstName", Map.of(
                            "type", "string",
                            "description", "First name of the lead"
                        ),
                        "lastName", Map.of(
                            "type", "string", 
                            "description", "Last name of the lead"
                        )
                    ),
                    "required", List.of("firstName", "lastName")
                )
            )
        );
        
        return new McpMessage(message.getId(), Map.of("tools", tools));
    }
    
    @SuppressWarnings("unchecked")
    private McpMessage handleToolCall(McpMessage message) {
        logger.info("Handling tools/call request");
        
        Map<String, Object> params = message.getParams();
        if (params == null) {
            return new McpMessage(message.getId(), 
                new McpMessage.McpError(-32602, "Invalid params"));
        }
        
        String toolName = (String) params.get("name");
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
        
        if (!"create_lead".equals(toolName)) {
            return new McpMessage(message.getId(), 
                new McpMessage.McpError(-32602, "Unknown tool: " + toolName));
        }
        
        if (arguments == null) {
            return new McpMessage(message.getId(), 
                new McpMessage.McpError(-32602, "Missing arguments"));
        }
        
        String firstName = (String) arguments.get("firstName");
        String lastName = (String) arguments.get("lastName");
        
        if (firstName == null || lastName == null) {
            return new McpMessage(message.getId(), 
                new McpMessage.McpError(-32602, "Missing required arguments: firstName and lastName"));
        }
        
        try {
            String result = leadCreationTool.createLead(firstName, lastName);
            return new McpMessage(message.getId(), Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", result
                )),
                "isError", false
            ));
        } catch (Exception e) {
            logger.error("Error calling create_lead tool: {}", e.getMessage(), e);
            return new McpMessage(message.getId(), Map.of(
                "content", List.of(Map.of(
                    "type", "text", 
                    "text", "Error creating lead: " + e.getMessage()
                )),
                "isError", true
            ));
        }
    }
    
    private void sendMessage(String clientId, McpMessage message) {
        Sinks.Many<String> sink = clientSinks.get(clientId);
        if (sink != null) {
            try {
                sink.tryEmitNext("data: " + message + "\n\n");
            } catch (Exception e) {
                logger.error("Error sending message to client {}: {}", clientId, e.getMessage());
            }
        }
    }
}
