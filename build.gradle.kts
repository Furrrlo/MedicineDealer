plugins {
    war
    id("org.gretty") version "3.0.3"

    id("java-convention")
    id("database")
    id("jaxb")
    id("npm")
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    providedCompile("javax.servlet:javax.servlet-api:3.1.0")
    compile("org.apache.commons:commons-csv:1.8")

    // Jersey

    val jerseyVersion = "2.30.1"
    providedCompile("jakarta.ws.rs:jakarta.ws.rs-api:2.1.6")
    runtime("org.glassfish.jersey.containers:jersey-container-servlet:$jerseyVersion")
    // Needed for custom parameter values
    compile("org.glassfish.jersey.core:jersey-server:$jerseyVersion")
    runtime("org.glassfish.jersey.inject:jersey-hk2:$jerseyVersion")

    providedCompile("jakarta.validation:jakarta.validation-api:2.0.2");
    runtime("org.glassfish.jersey.ext:jersey-bean-validation:$jerseyVersion")

    compile("org.glassfish.jersey.media:jersey-media-multipart:$jerseyVersion")

    // Guice

    val guiceVersion = "4.2.3"
    compile("org.glassfish.hk2:guice-bridge:2.6.1")
    compile("com.google.inject:guice:$guiceVersion")
    compile("com.google.inject.extensions:guice-assistedinject:$guiceVersion")

    // Logging

    val log4jVer = "2.13.2"
    compile("org.apache.logging.log4j:log4j-api:$log4jVer")
    runtime( "org.apache.logging.log4j:log4j-core:$log4jVer")

    runtime("org.slf4j:slf4j-api:1.7.30")
    runtime("org.slf4j:jcl-over-slf4j:1.7.30") // Needed by spring crypto stuff
    runtime("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVer")

    // JJWT

    compile("io.jsonwebtoken:jjwt-api:0.11.1")
    runtime("io.jsonwebtoken:jjwt-impl:0.11.1")
    runtime("io.jsonwebtoken:jjwt-orgjson:0.11.1")

    // JSON

    compile("org.json:json:20190722")

    // Test

    testCompile("junit:junit:4.12")
}

gretty {
    servletContainer = "tomcat85"
    loggingLevel = "INFO"
    fileLogEnabled = false
}

tasks.withType<Wrapper>().configureEach {
    gradleVersion = "6.4"
}