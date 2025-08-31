package com.sell.mcp.tool;

import com.sell.mcp.model.LeadRequest;
import com.sell.mcp.service.KylasApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LeadCreationTool {

    private static final Logger logger = LoggerFactory.getLogger(LeadCreationTool.class);

    private final KylasApiService kylasApiService;

    public LeadCreationTool(KylasApiService kylasApiService) {
        this.kylasApiService = kylasApiService;
    }

    public String createLead(String firstName, String lastName) {
        logger.info("Received lead creation request: firstName={}, lastName={}", firstName, lastName);

        try {
            LeadRequest leadRequest = new LeadRequest(firstName, lastName);
            String result = kylasApiService.createLead(leadRequest).block();
            logger.info("Lead creation result: {}", result);
            return "Lead created successfully: " + result;
        } catch (Exception e) {
            logger.error("Error creating lead: {}", e.getMessage(), e);
            return "Failed to create lead: " + e.getMessage();
        }
    }
}
