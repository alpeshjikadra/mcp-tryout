package com.sell.mcp.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpMessage {
    
    @JsonProperty("jsonrpc")
    private String jsonrpc = "2.0";
    
    @JsonProperty("id")
    private Object id;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("params")
    private Map<String, Object> params;
    
    @JsonProperty("result")
    private Object result;
    
    @JsonProperty("error")
    private McpError error;
    
    public McpMessage() {}
    
    public McpMessage(Object id, String method, Map<String, Object> params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }
    
    public McpMessage(Object id, Object result) {
        this.id = id;
        this.result = result;
    }
    
    public McpMessage(Object id, McpError error) {
        this.id = id;
        this.error = error;
    }
    
    // Getters and setters
    public String getJsonrpc() { return jsonrpc; }
    public void setJsonrpc(String jsonrpc) { this.jsonrpc = jsonrpc; }
    
    public Object getId() { return id; }
    public void setId(Object id) { this.id = id; }
    
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
    
    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }
    
    public McpError getError() { return error; }
    public void setError(McpError error) { this.error = error; }
    
    public static class McpError {
        @JsonProperty("code")
        private int code;
        
        @JsonProperty("message")
        private String message;
        
        @JsonProperty("data")
        private Object data;
        
        public McpError() {}
        
        public McpError(int code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public McpError(int code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
        
        // Getters and setters
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}
