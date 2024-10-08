package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.amazon.awscdk.services.dynamodb.*;

import java.util.Map;

public class CdkStack extends Stack {
    public CdkStack(final Construct scope, final String id, final DynamoDbStack dynamoDbStack) {
        this(scope, id, null, dynamoDbStack);
    }

    public CdkStack(final Construct scope, final String id, final StackProps props, final DynamoDbStack dynamoDbStack) {
        super(scope, id, props);

        // Define the Lambda function for proxy handling
        Function proxyLambdaFunction = Function.Builder.create(this, "ProxyLambdaFunction")
                .runtime(Runtime.JAVA_17)
                .handler("com.example.lambda.App::handleRequest") // Single handler for all routes
                .code(Code.fromAsset("../lambda/target/lambda-1.0-SNAPSHOT.jar")) // Path to the Lambda JAR
                .environment(Map.of(
                        "COURSES_TABLE", dynamoDbStack.coursesTable.getTableName() // Single table for both courses and reviews
                ))
                .memorySize(512)
                .timeout(Duration.seconds(30))
                .build();

        // Grant Lambda function permissions to read and write to the DynamoDB table
        dynamoDbStack.coursesTable.grantReadWriteData(proxyLambdaFunction);

        // Define the API Gateway
        RestApi api = RestApi.Builder.create(this, "CourseReviewApi")
                .restApiName("Course Review Service")
                .description("An API Gateway for managing courses, reviews, and authentication.")
                .build();

        // Define the /courses resource
        Resource coursesResource = api.getRoot().addResource("courses");
        LambdaIntegration proxyIntegration = new LambdaIntegration(proxyLambdaFunction);
        coursesResource.addMethod("ANY", proxyIntegration); // Use ANY to support all HTTP methods

        // Define the /reviews resource, even though reviews are part of the courses table
        Resource reviewsResource = api.getRoot().addResource("reviews");
        reviewsResource.addMethod("ANY", proxyIntegration);

        // Define the /authenticate resource
        Resource authResource = api.getRoot().addResource("authenticate");
        authResource.addMethod("POST", proxyIntegration);
    }
}
