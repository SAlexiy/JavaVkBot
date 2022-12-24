plugins {
    id("java")
}

group = "org.example"

repositories {
    mavenCentral()
}

dependencies {
    implementation("tech.ydb:ydb-sdk-core:2.0.0")
    implementation("tech.ydb.auth:yc-auth-provider:2.0.0")
    implementation("tech.ydb:ydb-sdk-table:2.0.0")

    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}