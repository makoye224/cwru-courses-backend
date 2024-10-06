package com.myorg;

import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class CdkStack extends Stack {
    public CdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public CdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Define the Lambda function
        Function lambdaFunction = Function.Builder.create(this, "EchoLambdaFunction")
                .runtime(Runtime.JAVA_17)
                .handler("com.example.lambda.App::handleRequest") // The handler method for Lambda
                .code(Code.fromAsset("../lambda/target/lambda-1.0-SNAPSHOT.jar")) // Path to the Lambda JAR
                .build();

        // Define the API Gateway with /echo endpoint
        RestApi api = RestApi.Builder.create(this, "EchoApi")
                .restApiName("Echo Service")
                .description("An API Gateway for echoing strings.")
                .build();

        // Define the /echo resource in the API Gateway
        Resource echoResource = api.getRoot().addResource("echo");

        // Integrate the Lambda function with the /echo resource
        LambdaIntegration echoIntegration = new LambdaIntegration(lambdaFunction);

        // Set up the POST method for /echo endpoint
        echoResource.addMethod("POST", echoIntegration); // Allow POST requests to /echo
    }
}
