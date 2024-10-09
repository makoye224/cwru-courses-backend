package com.example.lambda.models;

public class Review {
    private String reviewId;
    private String createdBy;
    private Double overall;
    private Double difficulty;
    private Double usefulness;
    private String major;
    private Boolean anonymous;
    private String additionalComments;
    private String tips;
    private String createdAt;
    private String professor;

    public Review() {
        // Default constructor
    }

    // Getters and setters for all fields

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Double getOverall() {
        return overall;
    }

    public void setOverall(Double overall) {
        this.overall = overall;
    }

    public Double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Double difficulty) {
        this.difficulty = difficulty;
    }

    public Double getUsefulness() {
        return usefulness;
    }

    public void setUsefulness(Double usefulness) {
        this.usefulness = usefulness;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + reviewId + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", overall=" + overall +
                ", difficulty=" + difficulty +
                ", usefulness=" + usefulness +
                ", major='" + major + '\'' +
                ", anonymous=" + anonymous +
                ", additionalComments='" + additionalComments + '\'' +
                ", tips='" + tips + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", professor='" + professor + '\'' +
                '}';
    }
}
