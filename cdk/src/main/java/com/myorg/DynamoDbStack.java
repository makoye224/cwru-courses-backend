package com.myorg;

import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class DynamoDbStack extends Stack {
    public final Table coursesTable;

    public DynamoDbStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public DynamoDbStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Create the DynamoDB table with courseId as the primary key
        coursesTable = Table.Builder.create(this, "CoursesTable")
                .partitionKey(Attribute.builder()
                        .name("name")  // Use code as partition key
                        .type(AttributeType.STRING)
                        .build())
                .sortKey(Attribute.builder()
                        .name("code")
                        .type(AttributeType.STRING)
                        .build())
                .tableName("Courses")
                .billingMode(BillingMode.PAY_PER_REQUEST)  // Adjust as needed
                .build();

        // GSI for querying by createdBy
        coursesTable.addGlobalSecondaryIndex(GlobalSecondaryIndexProps.builder()
                .indexName("CreatedByIndex")
                .partitionKey(Attribute.builder()
                        .name("createdBy")
                        .type(AttributeType.STRING)
                        .build())
                .projectionType(ProjectionType.ALL) // Include all fields in the query result
                .build());

// GSI for querying by course title
        coursesTable.addGlobalSecondaryIndex(GlobalSecondaryIndexProps.builder()
                .indexName("TitleIndex")
                .partitionKey(Attribute.builder()
                        .name("title")
                        .type(AttributeType.STRING)
                        .build())
                .projectionType(ProjectionType.ALL) // Include all fields in the query result
                .build());

//// GSI for querying by major
//        coursesTable.addGlobalSecondaryIndex(GlobalSecondaryIndexProps.builder()
//                .indexName("MajorIndex")
//                .partitionKey(Attribute.builder()
//                        .name("major")
//                        .type(AttributeType.STRING)
//                        .build())
//                .projectionType(ProjectionType.ALL)
//                .build());
//
//        // GSI for querying by professor
//        coursesTable.addGlobalSecondaryIndex(GlobalSecondaryIndexProps.builder()
//                .indexName("ProfessorIndex")
//                .partitionKey(Attribute.builder()
//                        .name("professor")
//                        .type(AttributeType.STRING)
//                        .build())
//                .projectionType(ProjectionType.ALL)
//                .build());
   }
}
