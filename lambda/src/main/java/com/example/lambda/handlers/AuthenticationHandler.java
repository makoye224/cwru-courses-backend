package com.example.lambda.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

    public APIGatewayProxyResponseEvent handleAuthenticationRequest(String httpMethod, String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        if ("POST".equals(httpMethod)) {
            logger.info("Authenticating user with body: {}", body);
            response.setBody("Authentication Logic");
        } else {
            response.setStatusCode(405); // Method Not Allowed
            response.setBody("Method Not Allowed");
            logger.warn("Invalid HTTP method: {}", httpMethod);
        }
        return response;
    }
}
