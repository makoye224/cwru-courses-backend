package com.example.lambda.dao;

import com.example.lambda.models.Course;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CourseDao {
    private static final Logger logger = LoggerFactory.getLogger(CourseDao.class);
    private final DynamoDbTable<Course> courseTable;
    private final Gson gson = new Gson();  // Gson instance for JSON conversions

    // Constructor to initialize the DynamoDbEnhancedClient and table
    public CourseDao() {
        // Initialize the DynamoDbClient with the correct region (no explicit credentials needed in Lambda)
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Create the DynamoDbEnhancedClient
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        // Map the Course class to the "Courses" table
        this.courseTable = enhancedClient.table("Courses", TableSchema.fromBean(Course.class));
    }

    // Method to save a course using DynamoDbEnhancedClient
    public void saveCourse(Course course) {
        try {
            // Convert aliases and reviews to JSON strings before saving
            if (course.getAliases() != null) {
                course.setAliasesJson(gson.toJson(course.getAliases()));
            }
            if (course.getReviews() != null) {
                course.setReviewsJson(gson.toJson(course.getReviews()));
            }

            // Save the course to the DynamoDB table
            courseTable.putItem(course);
            logger.info("Successfully saved course: " + course.getTitle());
        } catch (Exception e) {
            logger.error("Failed to save course", e);
        }
    }

    // Get a course using only the courseId (primary key)
    public Course getCourseById(String courseId) {
        Course course = courseTable.getItem(Key.builder()
                .partitionValue(courseId)
                .build());

        if (course != null) {
            try {
                // Convert JSON string back to list for aliases and reviews
                if (course.getAliasesJson() != null) {
                    course.setAliases(gson.fromJson(course.getAliasesJson(), List.class));
                }
                if (course.getReviewsJson() != null) {
                    course.setReviews(gson.fromJson(course.getReviewsJson(), List.class));
                }
            } catch (Exception e) {
                logger.error("Failed to deserialize JSON", e);
            }
        }

        return course;
    }

    // Get all courses created by a specific user using the "CreatedByIndex" GSI
    public List<Course> getCoursesByCreatedBy(String createdBy) {
        DynamoDbIndex<Course> createdByIndex = courseTable.index("CreatedByIndex");

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(createdBy)
                        .build()))
                .build();

        List<Course> courses = new ArrayList<>();
        Iterator<Page<Course>> results = createdByIndex.query(queryRequest).iterator();

        while (results.hasNext()) {
            Page<Course> page = results.next();
            courses.addAll(page.items());
        }

        return courses;
    }

    // Get all courses (scans the entire table)
    public List<Course> getAllCourses() {
        // Create a scan request to retrieve all courses
        ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder().build();

        // Use the scan operation to get all courses
        List<Course> courseList = new ArrayList<>();
        courseTable.scan(scanRequest).items().forEach(courseList::add);

        return courseList;  // Return the list of all courses
    }

    public void deleteCourseById(String courseId) {

    }
}
