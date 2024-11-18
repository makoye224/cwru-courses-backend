package com.example.lambda.util;

import com.example.lambda.models.CourseOutput;
import com.example.lambda.models.Review;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CourseSearch {

    private static final Logger logger = LoggerFactory.getLogger(CourseSearch.class);
    private static final double RELEVANCE_THRESHOLD = 0.25; // Adjusted threshold for stricter filtering

    // A method to calculate similarity between two strings
    private static double calculateSimilarity(String str1, String str2) {
        logger.info("Comparing: {} and {}", str1, str2);

        if (str1 == null || str2 == null || str1.trim().isEmpty() || str2.trim().isEmpty()) {
            return 0.0; // Return 0 if either string is null or empty
        }

        JaccardSimilarity jaccard = new JaccardSimilarity();
        double similarity = jaccard.apply(str1.toLowerCase(), str2.toLowerCase());
        logger.info("Jaccard similarity for '{}' and '{}' is {}", str1, str2, similarity);

        return similarity;
    }

    // A method to rank and filter courses based on a search query, only returning relevant ones
    public static List<CourseOutput> searchCourses(List<CourseOutput> courses, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            logger.warn("Search query is null or empty. Returning empty list.");
            return Collections.emptyList();
        }

        logger.info("Searching for: {}", searchQuery);
        return courses.stream()
                .map(course -> new AbstractMap.SimpleEntry<>(course, getRelevanceScore(course, searchQuery)))
                .filter(entry -> entry.getValue() > RELEVANCE_THRESHOLD) // Filter courses above threshold
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue())) // Sort by relevance score (descending)
                .map(Map.Entry::getKey) // Extract courses from the map
                .collect(Collectors.toList());
    }

    // A method to calculate the relevance score of a course based on the search query
    private static double getRelevanceScore(CourseOutput course, String searchQuery) {
        double titleScore = calculateSimilarity(course.getTitle(), searchQuery);
        double descriptionScore = calculateSimilarity(course.getDescription(), searchQuery);

        double aliasScore = course.getAliases().stream()
                .mapToDouble(alias -> calculateSimilarity(alias, searchQuery))
                .max().orElse(0.0);

        double reviewScore = course.getReviews().stream()
                .mapToDouble(review -> calculateReviewScore(review, searchQuery))
                .max().orElse(0.0);

        // Combine scores with adjustable weights
        double totalScore = titleScore * 0.4 + descriptionScore * 0.1 + aliasScore * 0.2 + reviewScore * 0.3;
        logger.info("Total relevance score for course '{}' is {}", course.getTitle(), totalScore);

        return totalScore;
    }

    // A method to calculate the relevance score for a review based on the professor and major fields
    private static double calculateReviewScore(Review review, String searchQuery) {
        double professorScore = calculateSimilarity(review.getProfessor(), searchQuery);
        double majorScore = calculateSimilarity(review.getMajor(), searchQuery);

        // Combine review scores with weights
        return professorScore * 0.6 + majorScore * 0.4;
    }
}
