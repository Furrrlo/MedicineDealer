import nu.studer.gradle.jooq.JooqEdition
import java.util.Properties

plugins {
    java
    war
    id("org.gretty") version "2.2.0"
    id("nu.studer.jooq")
    id("org.flywaydb.flyway") version "6.3.3"
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

    // Jersey

    providedCompile("jakarta.ws.rs:jakarta.ws.rs-api:2.1.6")
    runtime("org.glassfish.jersey.containers:jersey-container-servlet:2.30.1")
    runtime("org.glassfish.jersey.inject:jersey-hk2:2.30.1")

    // Database

    compile("mysql:mysql-connector-java:8.0.13")

    jooqRuntime("mysql:mysql-connector-java:8.0.13")
    compile("org.jooq:jooq")

    // Guice

    compile("org.glassfish.hk2:guice-bridge:2.6.1")
    compile("com.google.inject:guice:4.2.3'")
    compile("com.google.inject.extensions:guice-assistedinject:4.2.3")

    // Test

    testCompile("junit:junit:4.12")
}

gretty {
    servletContainer = "tomcat8"
    loggingLevel = "INFO"
    fileLogEnabled = false
}

val dbDriver = "com.mysql.cj.jdbc.Driver"
val dbSchema = "medicine_dealer"
val dbUrl = "jdbc:mysql://localhost:3306/"

val dbProperties = Properties()
file("database.properties").inputStream().use { dbProperties.load(it) }
val dbUser = dbProperties.getProperty("user") ?: throw IllegalArgumentException("Database user has not been specified")
val dbPassword = dbProperties.getProperty("password") ?: throw IllegalArgumentException("Database password has not been specified")

flyway {
    driver = dbDriver
    url = dbUrl
    user = dbUser
    password = dbPassword
    schemas = arrayOf(dbSchema)
    encoding = "UTF-8"
}

jooq {
    version = "3.11.9"
    edition = JooqEdition.OSS

    // See: https://github.com/etiennestuder/gradle-jooq-plugin/tree/master/example/use_kotlin_dsl
    "database"(sourceSets["main"]) {
        jdbc {
            driver = dbDriver
            url = "$dbUrl?serverTimezone=UTC"
            user = dbUser
            password = dbPassword
        }
        generator {
            generate {
                indentation = "    "
                newline = "\r\n"
            }
            database {
                name = "org.jooq.meta.mysql.MySQLDatabase"
                inputSchema = dbSchema
            }
            target {
                packageName = "$group.database"
                directory = "src/generated/jooq"
            }
        }
    }
}

tasks.named("generateDatabaseJooqSchemaSource").configure {
    dependsOn(tasks.flywayMigrate)
}

tasks.withType<Wrapper>().configureEach {
    gradleVersion = "5.2.1"
}