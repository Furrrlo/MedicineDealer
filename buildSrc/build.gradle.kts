plugins {
    `kotlin-dsl`
}

group = "gov.ismonnet.medicine.gradle"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("nu.studer:gradle-jooq-plugin:4.1")
    implementation("org.flywaydb:flyway-gradle-plugin:6.3.3")

    implementation("com.intershop.gradle.jaxb:jaxb-gradle-plugin:3.0.4")

    implementation("com.github.node-gradle:gradle-node-plugin:2.2.4")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
