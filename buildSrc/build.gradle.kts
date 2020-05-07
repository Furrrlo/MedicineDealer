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
}
