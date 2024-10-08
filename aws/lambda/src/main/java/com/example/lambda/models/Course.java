package com.example.lambda.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

import java.util.List;

@DynamoDbBean
public class Course {
    private String courseId;  // Partition key
    private String title;
    private String createdBy;  // Attribute, used in GSI
    private String createdAt;
    private String description;

    // Store lists as JSON strings in DynamoDB
    private String aliasesJson;  // JSON string for aliases
    private String reviewsJson;  // JSON string for reviews

    // Transient fields (not saved to DynamoDB) to store deserialized list versions
    private List<String> aliases;
    private List<String> prerequisites;
    private List<String> reviews;

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

    @DynamoDbAttribute("created at")
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
    @DynamoDbAttribute("aliasesJson")
    public String getAliasesJson() {
        return aliasesJson;
    }

    public void setAliasesJson(String aliasesJson) {
        this.aliasesJson = aliasesJson;
    }

    // Store reviews as JSON string in DynamoDB
    @DynamoDbAttribute("reviewsJson")
    public String getReviewsJson() {
        return reviewsJson;
    }

    public void setReviewsJson(String reviewsJson) {
        this.reviewsJson = reviewsJson;
    }

    // These fields are transient and not stored directly in DynamoDB as lists
    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
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
