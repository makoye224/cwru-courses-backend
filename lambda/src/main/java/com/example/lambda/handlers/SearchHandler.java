package com.example.lambda.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.lambda.dao.CourseDao;
import com.example.lambda.models.CourseOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

import java.util.List;



public class SearchHandler {

    private static final Logger logger = LoggerFactory.getLogger(SearchHandler.class);
    private final CourseDao courseDao;
    private final Gson gson;

    public SearchHandler(CourseDao courseDao) {
        this.courseDao = courseDao;
        this.gson = new Gson();
    }

    // Method to handle search requests via POST with string body
    public APIGatewayProxyResponseEvent handleSearchRequest(String searchString) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();


        try {
            // Ensure the search string is not null or empty
            if (searchString == null || searchString.trim().isEmpty()) {
                response.setStatusCode(400);
                response.setBody(serialize("No search parameters provided."));
                return response;
            }

            Query query = gson.fromJson(searchString, Query.class);

            String queryString = query.getQuery();

            // Trim the search string to remove leading/trailing spaces
            queryString  =  queryString.trim();

            logger.info("Searching for: " +  queryString);

            // Search courses using the provided search string
            List<CourseOutput> courses = courseDao.searchCourses(queryString);

            // If courses are found, return them as a JSON response
            if (!courses.isEmpty()) {
                response.setStatusCode(200);
                response.setBody(serialize(courses));
            } else {
                response.setStatusCode(404);
                response.setBody(serialize("No courses found for the given search criteria."));
            }

        } catch (Exception e) {
            logger.error("Error searching courses: {}", e.getMessage());
            response.setStatusCode(500);
            response.setBody(serialize("Error searching courses."));
        }

        return response;
    }

    // Method to serialize an object to JSON string using Gson
    private <T> String serialize(T object) {
        return gson.toJson(object);
    }

    private static class Query{
        private String query;

        public String getQuery() {
            return query;
        }
        public void setQuery(String query) {
            this.query = query;
        }
    }
}
