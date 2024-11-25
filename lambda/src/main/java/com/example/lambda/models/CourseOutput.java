package com.example.lambda.models;

import java.util.List;

public class CourseOutput {
    private String courseId;
    private String code;
    private String name;
    private String createdBy;
    private String createdAt;
    private String description;
    private List<String> aliases;
    private List<String> prerequisites;
    private List<Review> reviews;
    private String title;
    private List<String> professors;

    public CourseOutput() {
        // Default constructor
    }

    public List<String> getProfessors() {
        return professors;
    }

    public void setProfessors(List<String> professors) {
        this.professors = professors;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getters and setters for all fields
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return code + " " + name;
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

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "CourseOutput{" +
                "courseId='" + courseId + '\'' +
                ", title='" + name + " " + code + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", description='" + description + '\'' +
                ", aliases=" + aliases +
                ", prerequisites=" + prerequisites +
                ", reviews=" + reviews +
                '}';
    }
}
