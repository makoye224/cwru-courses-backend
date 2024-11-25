package com.example.lambda.dao;

import com.example.lambda.models.Course;
import com.example.lambda.models.CourseOutput;
import com.example.lambda.util.CourseConverter;
import com.example.lambda.util.CourseSearch;
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
import java.util.Map;
import java.util.stream.Collectors;

public class CourseDao {
    private static final Logger logger = LoggerFactory.getLogger(CourseDao.class);
    private final DynamoDbTable<Course> courseTable;

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
            // Save the course directly to DynamoDB
            courseTable.putItem(course);
            logger.info("Successfully saved course: " + course.getTitle());
        } catch (Exception e) {
            logger.error("Failed to save course", e);
            throw new RuntimeException(e);

        }
    }

    // Get all courses created by a specific user using the "CreatedByIndex" GSI
    public List<CourseOutput> getCoursesByCreatedBy(String createdBy) {
        DynamoDbIndex<Course> createdByIndex = courseTable.index("CreatedByIndex");

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(createdBy)
                        .build()))
                .build();

        List<CourseOutput> courseOutputs = new ArrayList<>();
        Iterator<Page<Course>> results = createdByIndex.query(queryRequest).iterator();

        while (results.hasNext()) {
            Page<Course> page = results.next();
            page.items().forEach(course -> courseOutputs.add(CourseConverter.convertToCourseOutput(course)));
        }

        return courseOutputs;
    }

    // Get all courses (scans the entire table)
    public List<CourseOutput> getAllCourses() {
        // Create a scan request to retrieve all courses
        ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder().build();

        // Use the scan operation to get all courses
        List<CourseOutput> courseOutputs = new ArrayList<>();
        courseTable.scan(scanRequest).items().forEach(course -> courseOutputs.add(CourseConverter.convertToCourseOutput(course)));

        return courseOutputs;  // Return the list of all courses
    }

    // Method to delete a course using DynamoDbEnhancedClient
    public void deleteCourse(String name, String code) {
        try {
            // Delete the course by using both the partition key (name) and sort key (code)
            courseTable.deleteItem(Key.builder()
                    .partitionValue(name)  // Use name as partition key
                    .sortValue(code)       // Use code as sort key
                    .build());

            logger.info("Successfully deleted course with name: " + name + " and code: " + code);
        } catch (Exception e) {
            logger.error("Failed to delete course with name: " + name + " and code: " + code, e);
        }
    }

    // Updated search method to filter and rank courses
    public List<CourseOutput> searchCourses(String searchString) {
        // Retrieve all courses (scanning the whole table)
        List<CourseOutput> allCourses = getAllCourses();
        if(allCourses.isEmpty()) {
            logger.info("No courses found in the database");
            throw new RuntimeException("No courses found in the database");
        }
        return CourseSearch.searchCourses(allCourses, searchString);
    }

    public CourseOutput getCourseByNameAndCode(String name, String code) {
        // Create a query request with both partition key (name) and sort key (code)
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(
                        Key.builder()
                                .partitionValue(name)   // Partition key: name
                                .sortValue(code)        // Sort key: code
                                .build()))
                .build();

        // Execute the query
        Iterator<Page<Course>> results = courseTable.query(queryRequest).iterator();
        if (results.hasNext()) {
            Page<Course> page = results.next();
            Course course = page.items().get(0); // Assuming we only expect one item

            // Convert the Course object to CourseOutput
            return CourseConverter.convertToCourseOutput(course);
        } else {
            logger.error("No course Found");
            throw new RuntimeException("No course Found");
        }
    }


}
