package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.amazon.awscdk.services.dynamodb.*;

import java.util.List;
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

        // Define the Lambda integration
        LambdaIntegration proxyIntegration = new LambdaIntegration(proxyLambdaFunction);

        // Define the /courses resource
        Resource coursesResource = api.getRoot().addResource("courses");
        coursesResource.addMethod("ANY", proxyIntegration, MethodOptions.builder()
                .authorizationType(AuthorizationType.NONE)
                .methodResponses(List.of(MethodResponse.builder()
                        .statusCode("200")
                        .responseParameters(Map.of(
                                "method.response.header.Access-Control-Allow-Origin", true,
                                "method.response.header.Access-Control-Allow-Methods", true // Enable CORS
                        ))
                        .build()))
                .build());

        // Enable CORS for the OPTIONS method on /courses
        coursesResource.addMethod("OPTIONS", new MockIntegration(IntegrationOptions.builder()
                .integrationResponses(List.of(IntegrationResponse.builder()
                        .statusCode("200")
                        .responseParameters(Map.of(
                                "method.response.header.Access-Control-Allow-Origin", "'*'",
                                "method.response.header.Access-Control-Allow-Methods", "'OPTIONS,GET,POST,PUT,DELETE'",
                                "method.response.header.Access-Control-Allow-Headers", "'Content-Type,Authorization,X-Amz-Date,X-Api-Key,X-Amz-Security-Token'"
                        ))
                        .build()))
                .passthroughBehavior(PassthroughBehavior.WHEN_NO_MATCH)
                .requestTemplates(Map.of("application/json", "{\"statusCode\": 200}"))
                .build()), MethodOptions.builder()
                .methodResponses(List.of(MethodResponse.builder()
                        .statusCode("200")
                        .responseParameters(Map.of(
                                "method.response.header.Access-Control-Allow-Origin", true,
                                "method.response.header.Access-Control-Allow-Methods", true,
                                "method.response.header.Access-Control-Allow-Headers", true
                        ))
                        .build()))
                .build());

        // Define the /reviews resource
        Resource reviewsResource = api.getRoot().addResource("reviews");
        reviewsResource.addMethod("ANY", proxyIntegration, MethodOptions.builder()
                .authorizationType(AuthorizationType.NONE)
                .methodResponses(List.of(MethodResponse.builder()
                        .statusCode("200")
                        .responseParameters(Map.of(
                                "method.response.header.Access-Control-Allow-Origin", true,
                                "method.response.header.Access-Control-Allow-Methods", true
                        ))
                        .build()))
                .build());

        // Enable CORS for the OPTIONS method on /reviews
        reviewsResource.addMethod("OPTIONS", new MockIntegration(IntegrationOptions.builder()
                .integrationResponses(List.of(IntegrationResponse.builder()
                        .statusCode("200")
                        .responseParameters(Map.of(
                                "method.response.header.Access-Control-Allow-Origin", "'*'",
                                "method.response.header.Access-Control-Allow-Methods", "'OPTIONS,GET,POST,PUT,DELETE'",
                                "method.response.header.Access-Control-Allow-Headers", "'Content-Type,Authorization,X-Amz-Date,X-Api-Key,X-Amz-Security-Token'"
                        ))
                        .build()))
                .passthroughBehavior(PassthroughBehavior.WHEN_NO_MATCH)
                .requestTemplates(Map.of("application/json", "{\"statusCode\": 200}"))
                .build()), MethodOptions.builder()
                .methodResponses(List.of(MethodResponse.builder()
                        .statusCode("200")
                        .responseParameters(Map.of(
                                "method.response.header.Access-Control-Allow-Origin", true,
                                "method.response.header.Access-Control-Allow-Methods", true,
                                "method.response.header.Access-Control-Allow-Headers", true
                        ))
                        .build()))
                .build());

        // Define the /authenticate resource
        Resource authResource = api.getRoot().addResource("authenticate");
        authResource.addMethod("POST", proxyIntegration);

        // Define the /search resource
        Resource searchResource = api.getRoot().addResource("search");
        searchResource.addMethod("POST", proxyIntegration, MethodOptions.builder()
                .authorizationType(AuthorizationType.NONE)
                .methodResponses(List.of(MethodResponse.builder()
                        .statusCode("200")
                        .responseParameters(Map.of(
                                "method.response.header.Access-Control-Allow-Origin", true,
                                "method.response.header.Access-Control-Allow-Methods", true // Enable CORS for POST
                        ))
                        .build()))
                .build());

        // Enable CORS for the OPTIONS method on /search
        searchResource.addMethod("OPTIONS", new MockIntegration(IntegrationOptions.builder()
                .integrationResponses(List.of(IntegrationResponse.builder()
                        .statusCode("200")
                        .responseParameters(Map.of(
                                "method.response.header.Access-Control-Allow-Origin", "'*'",
                                "method.response.header.Access-Control-Allow-Methods", "'OPTIONS,GET'",
                                "method.response.header.Access-Control-Allow-Headers", "'Content-Type,Authorization,X-Amz-Date,X-Api-Key,X-Amz-Security-Token'"
                        ))
                        .build()))
                .passthroughBehavior(PassthroughBehavior.WHEN_NO_MATCH)
                .requestTemplates(Map.of("application/json", "{\"statusCode\": 200}"))
                .build()), MethodOptions.builder()
                .methodResponses(List.of(MethodResponse.builder()
                        .statusCode("200")
                        .responseParameters(Map.of(
                                "method.response.header.Access-Control-Allow-Origin", true,
                                "method.response.header.Access-Control-Allow-Methods", true,
                                "method.response.header.Access-Control-Allow-Headers", true
                        ))
                        .build()))
                .build());
    }
}
