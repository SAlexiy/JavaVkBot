plugins {
    id("java")
}

group = "org.example"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.vk.api:sdk:1.0.14")

    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1:")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation(project(":YDBRepository"))
    implementation(project(":VKRepository"))
    implementation(project(":CSVRepository"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}