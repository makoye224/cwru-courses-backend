package com.example.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.lambda.dao.CourseDao;
import com.example.lambda.handlers.CoursesHandler;
import com.example.lambda.handlers.ReviewsHandler;
import com.example.lambda.handlers.AuthenticationHandler;
import com.example.lambda.handlers.SearchHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    CourseDao courseDao = new CourseDao();

    // Instantiate handler classes with injected DAOs
    private final CoursesHandler coursesHandler = new CoursesHandler(courseDao);
    private final ReviewsHandler reviewsHandler = new ReviewsHandler(courseDao);
    private final AuthenticationHandler authenticationHandler = new AuthenticationHandler();
    private final SearchHandler searchHandler = new SearchHandler(courseDao);


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String path = input.getPath(); // Get the path of the request
        String httpMethod = input.getHttpMethod(); // Get the HTTP method (GET, POST, etc.)
        String body = input.getBody(); // Get the request body (if any)

        // Log the request details using SLF4J logger
        logger.info("Path: {}", path);
        logger.info("HTTP Method: {}", httpMethod);
        logger.info("Body: {}", body);

        String courseId = null;
        String reviewId = null;

        // Extract courseId from the path parameters (if it's part of the path)
        if (input.getPathParameters() != null) {
            courseId = input.getPathParameters().get("courseId");
            reviewId = input.getPathParameters().get("reviewId");
        }

        // If courseId is not found in path parameters, check if it's in query string parameters
        if ((courseId == null || reviewId == null) && input.getQueryStringParameters() != null) {
            courseId = input.getQueryStringParameters().get("courseId");
            reviewId = input.getQueryStringParameters().get("reviewId");
        }

        // Log the extracted courseId
        logger.info("Extracted courseId: {}", courseId);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200); // Default success status

        // Route the request based on path
        if (path.startsWith("/courses")) {
            response = coursesHandler.handleCoursesRequest(httpMethod, body, courseId);
        } else if (path.startsWith("/reviews")) {
            response = reviewsHandler.handleReviewsRequest(httpMethod, body, courseId, reviewId);
        }
        else if (path.startsWith("/search")) {
            response = searchHandler.handleSearchRequest(body);
        }
        else if (path.startsWith("/authenticate")) {
            response = authenticationHandler.handleAuthenticationRequest(httpMethod, body);
        } else {
            // If the path does not match any known endpoint, return a 404 response
            response.setStatusCode(404);
            response.setBody("Not Found");
            logger.warn("Unknown path: {}", path);
        }

        // Add CORS headers to the response
        addCorsHeaders(response);

        return response;
    }

    // Helper method to add CORS headers to the response
    private void addCorsHeaders(APIGatewayProxyResponseEvent response) {
        response.setHeaders(Map.of(
                "Access-Control-Allow-Origin", "*", // Allow requests from any origin
                "Access-Control-Allow-Methods", "OPTIONS,GET,POST,PUT,DELETE", // Allow methods
                "Access-Control-Allow-Headers", "Content-Type,Authorization" // Allow headers
        ));
    }
}
