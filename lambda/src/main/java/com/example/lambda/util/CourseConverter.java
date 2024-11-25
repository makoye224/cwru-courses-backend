package com.example.lambda.util;

import com.example.lambda.models.Course;
import com.example.lambda.models.CourseOutput;
import com.example.lambda.models.Review;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CourseConverter {

    private static final Gson gson = new Gson();

    // Convert Course to CourseOutput
    public static CourseOutput convertToCourseOutput(Course course) {
        CourseOutput courseOutput = new CourseOutput();

        courseOutput.setCourseId(course.getCourseId());
        courseOutput.setCreatedBy(course.getCreatedBy());
        courseOutput.setCreatedAt(course.getCreatedAt());
        courseOutput.setDescription(course.getDescription());
        courseOutput.setCode(course.getCode());
        courseOutput.setName(course.getName());
        courseOutput.setTitle(course.getTitle());

        // Deserialize JSON strings back to lists
        if (course.getAliases() != null) {
            courseOutput.setAliases(gson.fromJson(course.getAliases(), List.class));
        }
        if (course.getPrerequisites() != null) {
            courseOutput.setPrerequisites(gson.fromJson(course.getPrerequisites(), List.class));
        }
        if (course.getReviews() != null) {
            Type reviewListType = new TypeToken<List<Review>>() {}.getType();
            courseOutput.setReviews(gson.fromJson(course.getReviews(), reviewListType));
        }
        if(course.getProfessors() != null) {
            Type professorListType = new TypeToken<List<String>>() {}.getType();
            courseOutput.setProfessors(gson.fromJson(course.getProfessors(), professorListType));
        }

        return courseOutput;
    }

    // Convert CourseOutput back to Course
    public static Course convertToCourse(CourseOutput courseOutput) {
        Course course = new Course();

        course.setCourseId(courseOutput.getCourseId());
        course.setCreatedBy(courseOutput.getCreatedBy());
        course.setCreatedAt(courseOutput.getCreatedAt());
        course.setDescription(courseOutput.getDescription());
        course.setCode(courseOutput.getCode());
        course.setName(courseOutput.getName());
        course.setTitle(courseOutput.getTitle());

        // Convert lists to JSON strings before setting them in the Course object
        if (courseOutput.getAliases() != null) {
            course.setAliases(gson.toJson(courseOutput.getAliases()));
        }
        if (courseOutput.getPrerequisites() != null) {
            course.setPrerequisites(gson.toJson(courseOutput.getPrerequisites()));
        }
        if (courseOutput.getReviews() != null) {
            Type reviewListType = new TypeToken<List<Review>>() {}.getType();
            course.setReviews(gson.toJson(courseOutput.getReviews(), reviewListType));
        }
        if (courseOutput.getProfessors() != null) {
            Type professorListType = new TypeToken<List<String>>() {}.getType();
            course.setProfessors(gson.toJson(courseOutput.getProfessors(), professorListType));
        }
        return course;
    }
}
