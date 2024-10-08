package com.example.lambda.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewsHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReviewsHandler.class);

    public APIGatewayProxyResponseEvent handleReviewsRequest(String httpMethod, String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        switch (httpMethod) {
            case "POST":
                logger.info("Creating a new review with body: {}", body);
                response.setBody("Create Review Logic");
                break;
            case "GET":
                logger.info("Getting reviews");
                response.setBody("Get Reviews Logic");
                break;
            default:
                response.setStatusCode(405); // Method Not Allowed
                response.setBody("Method Not Allowed");
                logger.warn("Invalid HTTP method: {}", httpMethod);
                break;
        }
        return response;
    }
}
