package com.example.lambda.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;


@DynamoDbBean
public class Course {
    private String courseId;  // Partition key
    private String title;
    private String createdBy;  // Attribute, used in GSI
    private String createdAt;
    private String description;

    // Store lists as JSON strings in DynamoDB
    private String aliases;  // JSON string for aliases
    private String prerequisites;  // JSON string for prerequisites
    private String reviews;  // JSON string for reviews

    // Constructor
    public Course() {
        // Default constructor
    }

    // Partition key for DynamoDB
    @DynamoDbPartitionKey
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    // Attribute for createdBy (used in the GSI)
    @DynamoDbAttribute("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @DynamoDbAttribute("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDbAttribute("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Store aliases as JSON string in DynamoDB
    @DynamoDbAttribute("aliases")
    public String getAliases() {
        return aliases;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    // Store prerequisites as JSON string in DynamoDB
    @DynamoDbAttribute("prerequisites")
    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    // Store reviews as JSON string in DynamoDB
    @DynamoDbAttribute("reviews")
    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", title='" + title + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", description='" + description + '\'' +
                ", aliases=" + aliases +
                ", prerequisites=" + prerequisites +
                ", reviews=" + reviews +
                '}';
    }
}
