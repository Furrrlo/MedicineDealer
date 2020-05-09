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
    mavenLocal()
    jcenter()
}

dependencies {
    implementation("nu.studer:gradle-jooq-plugin:4.1")
    implementation("org.flywaydb:flyway-gradle-plugin:6.3.3")

    implementation("com.intershop.gradle.jaxb:jaxb-gradle-plugin:3.0.4")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
