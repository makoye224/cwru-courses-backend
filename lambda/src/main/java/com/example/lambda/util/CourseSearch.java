package com.example.lambda.util;

import com.example.lambda.models.CourseOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CourseSearch {

    private static final Logger logger = LoggerFactory.getLogger(CourseSearch.class);

    // A method to search for courses based on a search query, prioritizing code > name > professor
    public static List<CourseOutput> searchCourses(List<CourseOutput> courses, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            logger.warn("Search query is null or empty. Returning empty list.");
            return Collections.emptyList();
        }

        logger.info("Searching for: {}", searchQuery);

        // Filter courses based on the priority order: code > name > professor
        List<CourseOutput> filteredCourses = courses.stream()
                .filter(course -> matchesCode(course, searchQuery))
                .collect(Collectors.toList());

        if (filteredCourses.isEmpty()) {
            filteredCourses = courses.stream()
                    .filter(course -> matchesName(course, searchQuery))
                    .collect(Collectors.toList());
        }

        if (filteredCourses.isEmpty()) {
            filteredCourses = courses.stream()
                    .filter(course -> matchesProfessor(course, searchQuery))
                    .collect(Collectors.toList());
        }

        return filteredCourses;
    }

    // Check if the course code matches the search query
    private static boolean matchesCode(CourseOutput course, String searchQuery) {
        if (course.getCode() == null || searchQuery == null) {
            return false;
        }

        // Normalize both the course code and search query (remove all Unicode whitespace and convert to lowercase)
        String normalizedCode = course.getCode()
                .replaceAll("\\s+", "")        // Remove standard whitespace
                .replaceAll("\\u00A0", "")     // Remove non-breaking spaces
                .toLowerCase();
        String normalizedQuery = searchQuery
                .replaceAll("\\s+", "")        // Remove standard whitespace
                .replaceAll("\\u00A0", "")     // Remove non-breaking spaces
                .toLowerCase();

        logger.info("query: {}", normalizedQuery);
        logger.info("courseCode: {}", normalizedCode);

        // Check for an exact match
        if (normalizedCode.equals(normalizedQuery)) {
            return true;
        }

        // Check for a partial match
        return normalizedCode.contains(normalizedQuery);
    }


    // Check if the course name matches the search query
    private static boolean matchesName(CourseOutput course, String searchQuery) {
        return course.getName() != null && course.getName().toLowerCase().contains(searchQuery.toLowerCase());
    }

    // Check if the course professor(s) match the search query
    private static boolean matchesProfessor(CourseOutput course, String searchQuery) {
        return course.getProfessors() != null && course.getProfessors().stream()
                .anyMatch(professor -> professor.toLowerCase().contains(searchQuery.toLowerCase()));
    }
}
