package com.example.lambda.validators;

import com.example.lambda.models.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseValidator {

    // Method to validate the provided course object
    public List<String> validateCourse(Course course) {
        List<String> errors = new ArrayList<>();

        // Check if the courseId is provided
        if (course.getCourseId() == null || course.getCourseId().trim().isEmpty()) {
            errors.add("Course ID is required.");
        }

        // Check if the title is provided
        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            errors.add("Course title is required.");
        }

        // Check if createdBy is provided
        if (course.getCreatedBy() == null || course.getCreatedBy().trim().isEmpty()) {
            errors.add("Created By field is required.");
        }

        // Check if description is provided (optional, if required, uncomment the below check)
        /*
        if (course.getDescription() == null || course.getDescription().trim().isEmpty()) {
            errors.add("Course description is required.");
        }
        */

        return errors;  // Return the list of validation errors (empty if no errors)
    }

    // Optional: Method to check if the course object has valid fields for update operations
    public List<String> validateForUpdate(Course course) {
        List<String> errors = new ArrayList<>();

        // Check if the courseId is provided (required for updating a course)
        if (course.getCourseId() == null || course.getCourseId().trim().isEmpty()) {
            errors.add("Course ID is required for updates.");
        }

        // Additional update-specific validations can be added here

        return errors;  // Return the list of validation errors for update operations
    }
}
