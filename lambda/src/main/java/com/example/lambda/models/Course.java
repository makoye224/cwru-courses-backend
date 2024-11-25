package com.example.lambda.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

@DynamoDbBean
public class Course {
    private String courseId;  // Unique identifier
    private String code;      // e.g., CSDS 101
    private String name;      // e.g., Discrete Mathematics
    private String createdBy;
    private String createdAt;
    private String description;
    private String aliases;
    private String prerequisites;
    private String reviews;
    private String title;
    private String professors;

    @DynamoDbSortKey
    @DynamoDbAttribute("code")
    public String getCode() {
        return code;
    }

    public String getProfessors() {
        return professors;
    }

    public void setProfessors(String professors) {
        this.professors = professors;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAliases() {
        return aliases;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

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
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", title='" + code + " " + name + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", description='" + description + '\'' +
                ", aliases=" + aliases +
                ", prerequisites=" + prerequisites +
                ", reviews=" + reviews +
                '}';
    }
}
