package com.example.lambda.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.lambda.dao.CourseDao;
import com.example.lambda.models.CourseOutput;
import com.example.lambda.models.Review;
import com.example.lambda.util.CourseConverter;
import com.example.lambda.validators.ReviewValidator;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class ReviewsHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReviewsHandler.class);
    private final CourseDao courseDao;
    private final Gson gson;
    private final ReviewValidator validator;

    public ReviewsHandler(CourseDao courseDao) {
        this.courseDao = courseDao;
        this.validator = new ReviewValidator();
        this.gson = new Gson();
    }

    public APIGatewayProxyResponseEvent handleReviewsRequest(String httpMethod, String body, String courseId, String reviewId) {
        logger.info("entered handleReviewsRequest");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        switch (httpMethod) {
            case "POST":
                return createReview(body, courseId);
            case "PUT":
                return updateReview(body, courseId, reviewId);
            case "DELETE":
                return deleteReview(courseId, reviewId);
            default:
                response.setStatusCode(405); // Method Not Allowed
                response.setBody(serialize("Method Not Allowed"));
                logger.warn("Invalid HTTP method: {}", httpMethod);
                break;
        }
        return response;
    }

    // Method to create a new review
    private APIGatewayProxyResponseEvent createReview(String body, String courseId) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            // Parse the request body into a Review object
            Review newReview = gson.fromJson(body, Review.class);

            logger.info("parsed review {}", newReview);

            // Validate the review
            List<String> validationErrors = validator.validateReview(newReview);

            if (!validationErrors.isEmpty()) {
                // If there are errors, return a 400 Bad Request response with the error messages
                response.setStatusCode(400);
                response.setBody(serialize(String.join(", ", validationErrors)));  // Combine errors into a single string
                return response;
            }

            // Get the course by ID
            CourseOutput courseOutput = courseDao.getCourseById(courseId);

            if (courseOutput == null) {
                response.setStatusCode(404);
                response.setBody(serialize("Course not found"));
                return response;
            }

            // Add the new review to the course
            List<Review> reviews = courseOutput.getReviews();
            reviews.add(newReview);
            courseOutput.setReviews(reviews);

            // Save the updated course with the new review
            courseDao.saveCourse(CourseConverter.convertToCourse(courseOutput));

            response.setStatusCode(201);  // Created
            response.setBody(serialize("Review added successfully"));
        } catch (Exception e) {
            logger.error("Error creating review: {}", e.getMessage());
            response.setStatusCode(500);  // Internal server error
            response.setBody(serialize("Error creating review"));
        }

        return response;
    }

    // Method to update an existing review
    private APIGatewayProxyResponseEvent updateReview(String body, String courseId, String reviewId) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            // Parse the request body into a Review object
            Review updatedReview = gson.fromJson(body, Review.class);
            updatedReview.setReviewId(reviewId);  // Ensure the reviewId remains the same

            // Validate the course
            List<String> validationErrors = validator.validateForUpdate(updatedReview);

            if (!validationErrors.isEmpty()) {
                // If there are errors, return a 400 Bad Request response with the error messages
                response.setStatusCode(400);
                response.setBody(serialize(String.join(", ", validationErrors)));  // Combine errors into a single string
                return response;
            }

            // Get the course by ID
            CourseOutput courseOutput = courseDao.getCourseById(courseId);

            if (courseOutput == null) {
                response.setStatusCode(404);
                response.setBody(serialize("Course not found"));
                return response;
            }

            // Find and update the review in the course's reviews list
            List<Review> reviews = courseOutput.getReviews();
            boolean reviewFound = false;

            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getReviewId().equals(reviewId)) {
                    reviews.set(i, updatedReview);
                    reviewFound = true;
                    break;
                }
            }

            if (!reviewFound) {
                response.setStatusCode(404);
                response.setBody(serialize("Review not found"));
                return response;
            }

            // Save the updated course with the modified review
            courseOutput.setReviews(reviews);
            courseDao.saveCourse(CourseConverter.convertToCourse(courseOutput));

            response.setStatusCode(200);  // OK
            response.setBody(serialize("Review updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating review: {}", e.getMessage());
            response.setStatusCode(500);  // Internal server error
            response.setBody(serialize("Error updating review"));
        }

        return response;
    }

    // Method to delete an existing review
    private APIGatewayProxyResponseEvent deleteReview(String courseId, String reviewId) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            // Get the course by ID
            CourseOutput courseOutput = courseDao.getCourseById(courseId);

            if (courseOutput == null) {
                response.setStatusCode(404);
                response.setBody(serialize("Course not found"));
                return response;
            }

            // Remove the review from the course's reviews list
            List<Review> reviews = courseOutput.getReviews();
            boolean reviewFound = reviews.removeIf(review -> review.getReviewId().equals(reviewId));

            if (!reviewFound) {
                response.setStatusCode(404);
                response.setBody(serialize("Review not found"));
                return response;
            }

            // Save the updated course without the deleted review
            courseOutput.setReviews(reviews);
            courseDao.saveCourse(CourseConverter.convertToCourse(courseOutput));

            response.setStatusCode(200);  // OK
            response.setBody(serialize("Review deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting review: {}", e.getMessage());
            response.setStatusCode(500);  // Internal server error
            response.setBody(serialize("Error deleting review"));
        }

        return response;
    }

    // Method to serialize an object to JSON string using Gson
    private <T> String serialize(T object) {
        return gson.toJson(object);
    }
}
