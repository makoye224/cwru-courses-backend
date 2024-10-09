package com.example.lambda.validators;

import com.example.lambda.models.Course;
import com.example.lambda.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewValidator {

    public List<String> validateReview(Review review) {
        List<String> errors = new ArrayList<>();

        // Validate reviewId
        if (review.getReviewId() == null || review.getReviewId().trim().isEmpty()) {
            errors.add("Review ID is required.");
        }

        // Validate createdBy
        if (review.getCreatedBy() == null || review.getCreatedBy().trim().isEmpty()) {
            errors.add("Created By field is required.");
        }

        // Validate overall rating
        if (review.getOverall() == null || review.getOverall() < 1 || review.getOverall() > 10) {
            errors.add("Overall rating must be between 1 and 10.");
        }

        // Validate difficulty
        if (review.getDifficulty() == null || review.getDifficulty() < 1 || review.getDifficulty() > 10) {
            errors.add("Difficulty rating must be between 1 and 10.");
        }

        // Validate usefulness
        if (review.getUsefulness() == null || review.getUsefulness() < 1 || review.getUsefulness() > 10) {
            errors.add("Usefulness rating must be between 1 and 10.");
        }

        // Validate anonymous
        if (review.getAnonymous() == null) {
            errors.add("Anonymous field is required.");
        }

        // Validate createdAt (optional, based on String or LocalDateTime)
        if (review.getCreatedAt() == null || review.getCreatedAt().trim().isEmpty()) {
            errors.add("Creation date is required.");
        }

        return errors;
    }

    public List<String> validateForUpdate(Review review) {
        List<String> errors = new ArrayList<>();

        // Validate reviewId for update
        if (review.getReviewId() == null || review.getReviewId().trim().isEmpty()) {
            errors.add("Review ID is required for updates.");
        }

        // Additional validations for updates can be added here
        return errors;
    }
}
