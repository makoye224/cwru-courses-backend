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

import java.util.*;

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

    public APIGatewayProxyResponseEvent handleReviewsRequest(String httpMethod, String body, String name, String code, String reviewId) {
        logger.info("entered handleReviewsRequest");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        switch (httpMethod) {
            case "POST":
                return createReview(body, name, code);
            case "PUT":
                return updateReview(body, name, code, reviewId);
            case "DELETE":
                return deleteReview(name, code, reviewId);
            default:
                response.setStatusCode(405); // Method Not Allowed
                response.setBody(serialize("Method Not Allowed"));
                logger.warn("Invalid HTTP method: {}", httpMethod);
                break;
        }
        return response;
    }

    private APIGatewayProxyResponseEvent createReview(String body, String name, String code) {
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

            // Get the course by name and code
            CourseOutput courseOutput = courseDao.getCourseByNameAndCode(name, code);

            if (courseOutput == null) {
                response.setStatusCode(404);
                response.setBody(serialize("Course not found"));
                return response;
            }

            // Extract professor from the new review and update the professors list if necessary
            String professorName = newReview.getProfessor();
            List<String> professorList = courseOutput.getProfessors();

            // Add the professor to the list if it's not already there
             professorList.add(professorName);

            // Update the professors list in the course output
            courseOutput.setProfessors(professorList);

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
    private APIGatewayProxyResponseEvent updateReview(String body, String name, String code, String reviewId) {
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
            CourseOutput courseOutput = courseDao.getCourseByNameAndCode(name, code);

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

            // Extract the professor from the updated review and ensure the professors list is up-to-date
            String professorName = updatedReview.getProfessor();
            List<String> professorList = courseOutput.getProfessors();

            // Add the professor to the list if it's not already there
            if (!professorList.contains(professorName)) {
                professorList.add(professorName);
            }

            // Update the professors list in the course output
            courseOutput.setProfessors(professorList);

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
    private APIGatewayProxyResponseEvent deleteReview(String name, String code, String reviewId) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            // Get the course by ID
            CourseOutput courseOutput = courseDao.getCourseByNameAndCode(name, code);

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

            // If the review was deleted, we may need to update the professors list.
            // Check if the professor associated with the review should be removed.
            Review removedReview = null;
            for (Review review : courseOutput.getReviews()) {
                if (review.getReviewId().equals(reviewId)) {
                    removedReview = review;
                    break;
                }
            }

            if (removedReview != null) {
                String professorName = removedReview.getProfessor();
                List<String> professorList =courseOutput.getProfessors();

                // Remove the professor from the list if they exist
                professorList.remove(professorName);

                // Update the professors list in the course output
                courseOutput.setProfessors(Collections.singletonList(String.join(",", professorList)));
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
