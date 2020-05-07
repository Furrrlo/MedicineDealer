plugins {
    java
    war
    id("org.gretty") version "2.2.0"
}

group = "gov.ismonnet.medicine"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    providedCompile("javax.servlet:javax.servlet-api:3.1.0")
    providedCompile("jakarta.ws.rs:jakarta.ws.rs-api:2.1.6")

    runtime("org.glassfish.jersey.containers:jersey-container-servlet:2.30.1")
    runtime("org.glassfish.jersey.inject:jersey-hk2:2.30.1")

    testCompile("junit:junit:4.12")
}

gretty {
    servletContainer = "tomcat8"
    loggingLevel = "INFO"
    fileLogEnabled = false
}

tasks.withType<Wrapper>().configureEach {
    gradleVersion = "5.2.1"
}