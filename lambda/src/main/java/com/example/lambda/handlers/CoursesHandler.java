package com.example.lambda.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.lambda.dao.CourseDao;
import com.example.lambda.models.Course;
import com.example.lambda.models.CourseOutput;
import com.example.lambda.util.CourseConverter;
import com.example.lambda.validators.CourseValidator;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class CoursesHandler {

    private static final Logger logger = LoggerFactory.getLogger(CoursesHandler.class);

    private final Gson gson;
    private final CourseDao courseDao;
    // Instantiate the validator
    CourseValidator validator;

    public CoursesHandler(CourseDao courseDao) {
        this.courseDao = courseDao;
        this.gson = new Gson();  // Gson instance for serialization/deserialization
        validator = new CourseValidator();
    }

    public APIGatewayProxyResponseEvent handleCoursesRequest(String httpMethod, String body, String name, String code) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        if ("POST".equalsIgnoreCase(httpMethod)) {
            // Handle course creation
            return createCourse(body);
        }
        else if("DELETE".equalsIgnoreCase(httpMethod)) {
           return deleteCourse(name, code);
        }
        else if ("GET".equalsIgnoreCase(httpMethod)) {
            // Handle getting course(s)
            if (name != null && !name.isEmpty() && code != null && !code.isEmpty()) {
                // If courseId is provided, fetch the specific course
                return getSingleCourse(name, code);
            } else {
                // If no courseId is provided, fetch all courses
                return getAllCourses();
            }
        }

        // Return 405 for unsupported HTTP methods
        response.setStatusCode(405);
        response.setBody("Method Not Allowed");
        return response;
    }

    // Handle course creation or update
    private APIGatewayProxyResponseEvent createCourse(String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            // Log the incoming request body
            logger.info("Request body: {}", body);

            // Deserialize the request body into a Course object using Gson
            CourseOutput courseOutput = gson.fromJson(body, CourseOutput.class);

            logger.info("Parsed Body: {}", courseOutput);

            Course course = CourseConverter.convertToCourse(courseOutput);

            logger.info("course to post: {}", course);

            // Validate the course
            List<String> validationErrors = validator.validateCourse(course);

            if (!validationErrors.isEmpty()) {
                // If there are errors, return a 400 Bad Request response with the error messages
                response.setStatusCode(400);
                response.setBody(String.join(", ", validationErrors));  // Combine errors into a single string
                return response;
            }
                // Save the new course
                courseDao.saveCourse(course);

                // Log the creation
                logger.info("Created new course: {}", course);

                // Set response success message for creation
                response.setStatusCode(201);  // Created
                response.setBody(serialize("Course created successfully!"));


        } catch (Exception e) {
            // Handle error during parsing or saving
            logger.error("Error parsing or saving course: {}", e.getMessage());
            response.setStatusCode(400);  // Bad request
            response.setBody("Invalid request format.");
        }

        return response;
    }

    // Handle getting a single course by courseId
    private APIGatewayProxyResponseEvent getSingleCourse(String name, String code) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            // Get a single course by courseId
            CourseOutput course = courseDao.getCourseByNameAndCode(name, code);
            if (course != null) {
                response.setStatusCode(200);
                response.setBody(serialize(course));
            } else {
                response.setStatusCode(404);
                response.setBody("Course not found");
            }
        } catch (Exception e) {
            logger.error("Error fetching course by ID: {}", e.getMessage());
            response.setStatusCode(500);  // Internal server error
            response.setBody("Error fetching course.");
        }

        return response;
    }

    // Handle getting all courses
    private APIGatewayProxyResponseEvent getAllCourses() {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            // Get all courses
            List<CourseOutput> courses = courseDao.getAllCourses();
            if (!courses.isEmpty()) {
                response.setStatusCode(200);
                response.setBody(serialize(courses));  // Serialize the list of courses
            } else {
                response.setStatusCode(404);
                response.setBody("No courses found");
            }
        } catch (Exception e) {
            logger.error("Error fetching all courses: {}", e.getMessage());
            response.setStatusCode(500);  // Internal server error
            response.setBody("Error fetching courses.");
        }

        return response;
    }

    // Handle deleting a course by courseId
    private APIGatewayProxyResponseEvent deleteCourse(String name, String code) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            // Check if the course exists
            CourseOutput course = courseDao.getCourseByNameAndCode(name, code);

            if (course != null) {
                // Delete the course
                courseDao.deleteCourse(name, code);

                // Set response success message for deletion
                response.setStatusCode(200);  // OK
                response.setBody(serialize("Course deleted successfully!"));
            } else {
                // If the course is not found
                response.setStatusCode(404);  // Not found
                response.setBody("Course not found");
            }

        } catch (Exception e) {
            // Handle any error during deletion
            logger.error("Error deleting course: {}", e.getMessage());
            response.setStatusCode(500);  // Internal server error
            response.setBody("Error deleting course.");
        }

        return response;
    }


    // Method to serialize an object to JSON string using Gson
    private <T> String serialize(T object) {
        return gson.toJson(object);
    }
}
