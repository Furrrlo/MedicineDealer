import nu.studer.gradle.jooq.JooqEdition
import java.util.*

plugins {
    java
    id("nu.studer.jooq")
    id("org.flywaydb.flyway")
}

val dbProperties = Properties()
file("database.properties").inputStream().use { dbProperties.load(it) }
val dbUser = dbProperties.getProperty("user") ?: throw IllegalArgumentException("Database user has not been specified")
val dbPassword = dbProperties.getProperty("password") ?: throw IllegalArgumentException("Database password has not been specified")

val dbDriver = dbProperties.getProperty("database.driver", "com.mysql.cj.jdbc.Driver")
val dbSchema = dbProperties.getProperty("database.schema", "medicine_dealer")
val dbUrl = dbProperties.getProperty("database.url") ?: "jdbc:mysql://localhost:${dbProperties.getProperty("database.port", "3306")}/"

dependencies {
    compile("mysql:mysql-connector-java:8.0.13")
    compile("org.apache.tomcat:tomcat-jdbc:10.0.0-M4")
    compile("org.flywaydb:flyway-core:6.3.3")

    jooqRuntime("mysql:mysql-connector-java:8.0.13")
    compile("org.jooq:jooq")

    compile("org.springframework.security:spring-security-crypto:5.3.2.RELEASE")
}

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
                isJavaTimeTypes = true
            }
            database {
                name = "org.jooq.meta.mariadb.MariaDBDatabase"
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
    inputs.files(project.fileTree("src/main/resources/db/migration"))
            .withPropertyName("migrations")
            .withPathSensitivity(PathSensitivity.RELATIVE)
}
