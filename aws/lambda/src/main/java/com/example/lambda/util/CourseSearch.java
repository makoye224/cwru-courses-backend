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
    private static final double RELEVANCE_THRESHOLD = 0.025; // Set your threshold here

    // A method to calculate similarity between two strings
    private static double calculateSimilarity(String str1, String str2) {
        logger.info("Comparing: " + str1 + " and " + str2);

        // If either string is null, return 0 similarity
        if (str1 == null || str2 == null) {
            return 0;
        }

        // If the strings are exactly the same (case-insensitive), return 1
        if (str1.equalsIgnoreCase(str2)) {
            return 1.0;
        }

        // Split the strings into words
        String[] words1 = str1.split("\\s+"); // Split on spaces, tabs, etc.
        String[] words2 = str2.split("\\s+");

        // Count the matching words and substrings
        int matchingWordsCount = 0;
        for (String word1 : words1) {
            for (String word2 : words2) {
                // Check for exact match or substring match (case-insensitive)
                if (word1.equalsIgnoreCase(word2) ||
                        word1.toLowerCase().contains(word2.toLowerCase()) ||
                        word2.toLowerCase().contains(word1.toLowerCase())) {
                    matchingWordsCount++;
                    break;  // Only count one match per word1
                }
            }
        }

        // Calculate similarity score (number of matches / longer string length)
        int maxWordsLength = Math.max(words1.length, words2.length);
        double similarity = (double) matchingWordsCount / maxWordsLength;

        logger.info("Similarity for: " + str1 + " and " + str2 + " is " + similarity);
        return similarity;
    }

    // A method to rank and filter courses based on a search query, only returning relevant ones
    public static List<CourseOutput> searchCourses(List<CourseOutput> courses, String searchQuery) {
        logger.info("Searching for: " + searchQuery);
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
                .max().orElse(0);

        // Calculate review score based on professor and major
        double reviewScore = course.getReviews().stream()
                .mapToDouble(review -> calculateReviewScore(review, searchQuery))
                .max().orElse(0);

        // Combine scores, weights can be adjusted
        return titleScore * 0.3 + descriptionScore * 0.2 + aliasScore * 0.2 + reviewScore * 0.3;
    }

    // A method to calculate the relevance score for a review based on the professor and major fields
    private static double calculateReviewScore(Review review, String searchQuery) {
        double professorScore = calculateSimilarity(review.getProfessor(), searchQuery);
        double majorScore = calculateSimilarity(review.getMajor(), searchQuery);

        // Combine review scores with adjustable weights for professor and major
        return professorScore * 0.6 + majorScore * 0.4; // Adjust weights as necessary
    }
}
