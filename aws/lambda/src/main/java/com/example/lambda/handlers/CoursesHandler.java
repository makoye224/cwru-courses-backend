package com.example.lambda.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.lambda.dao.CourseDao;
import com.example.lambda.models.Course;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CoursesHandler {

    private static final Logger logger = LoggerFactory.getLogger(CoursesHandler.class);

    private final Gson gson;
    private final CourseDao courseDao;

    public CoursesHandler(CourseDao courseDao) {
        this.courseDao = courseDao;
        this.gson = new Gson();  // Gson instance for serialization/deserialization
    }

    public APIGatewayProxyResponseEvent handleCoursesRequest(String httpMethod, String body, String courseId) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        if ("POST".equalsIgnoreCase(httpMethod)) {
            // Handle course creation
            return createCourse(body);
        } else if ("GET".equalsIgnoreCase(httpMethod)) {
            // Handle getting course(s)
            if (courseId != null && !courseId.isEmpty()) {
                // If courseId is provided, fetch the specific course
                return getSingleCourse(courseId);
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

    // Handle course creation
    private APIGatewayProxyResponseEvent createCourse(String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            // Log the incoming request body
            logger.info("Request body: {}", body);

            // Deserialize the request body into a Course object using Gson
            Course course = gson.fromJson(body, Course.class);

            // Ensure the required fields are populated if missing from the request
            if (course.getCourseId() == null || course.getCourseId().isEmpty()) {
                course.setCourseId(UUID.randomUUID().toString());  // Generate unique courseId if not provided
            }
            if (course.getCreatedAt() == null || course.getCreatedAt().isEmpty()) {
                course.setCreatedAt(Instant.now().toString());  // Set creation timestamp if not provided
            }
            if (course.getCreatedBy() == null || course.getCreatedBy().isEmpty()) {
                course.setCreatedBy("makoye");  // Set default createdBy if not provided
            }

            // Save the course using the DAO
            courseDao.saveCourse(course);

            // Log the parsed object
            logger.info("Parsed course: {}", course);

            // Set response success message
            response.setStatusCode(201);  // Created
            response.setBody("Course created successfully!");

        } catch (Exception e) {
            // Handle error during parsing or saving
            logger.error("Error parsing course creation request: {}", e.getMessage());
            response.setStatusCode(400);  // Bad request
            response.setBody("Invalid request format.");
        }

        return response;
    }

    // Handle getting a single course by courseId
    private APIGatewayProxyResponseEvent getSingleCourse(String courseId) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            // Get a single course by courseId
            Course course = courseDao.getCourseById(courseId);
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
            List<Course> courses = courseDao.getAllCourses();
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

    // Method to serialize an object to JSON string using Gson
    private <T> String serialize(T object) {
        return gson.toJson(object);
    }
}
