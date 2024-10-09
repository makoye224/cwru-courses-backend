package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class CdkApp {
    public static void main(final String[] args) {
        App app = new App();

        // Create the DynamoDB Stack
        DynamoDbStack dynamoDbStack = new DynamoDbStack(app, "DynamoDbStack", StackProps.builder()
                .env(Environment.builder()
                        .account("955039243667")
                        .region("us-east-1")
                        .build())
                .build());

        // Create the CDK stack that uses the DynamoDB tables
        new CdkStack(app, "CdkStack", StackProps.builder()
                .env(Environment.builder()
                        .account("955039243667")
                        .region("us-east-1")
                        .build())
                .build(), dynamoDbStack);

        // Synthesize the application
        app.synth();
    }
}
